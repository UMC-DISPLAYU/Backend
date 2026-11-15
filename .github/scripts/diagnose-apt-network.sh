#!/usr/bin/env bash
set -u

printf 'Diagnostic UTC time: %s\n' "$(date -u +%FT%TZ)"
printf 'Runner OS=%s architecture=%s image=%s version=%s\n' \
  "${RUNNER_OS:-unknown}" "${RUNNER_ARCH:-unknown}" \
  "${ImageOS:-unknown}" "${ImageVersion:-unknown}"

if command -v python3 >/dev/null 2>&1; then
  python3 - <<'PY'
import glob
import ipaddress
import pathlib
import re
import urllib.parse

os_release = pathlib.Path('/etc/os-release')
if os_release.exists():
    for line in os_release.read_text().splitlines():
        if line.startswith(('NAME=', 'VERSION=', 'VERSION_ID=')):
            print(line)

resolver = pathlib.Path('/etc/resolv.conf')
if resolver.exists():
    for line in resolver.read_text().splitlines():
        parts = line.split()
        if len(parts) >= 2 and parts[0] == 'nameserver':
            try:
                print('nameserver', ipaddress.ip_address(parts[1]))
            except ValueError:
                print('nameserver [invalid address omitted]')
        elif parts and parts[0] == 'options':
            print('resolver options', ' '.join(p for p in parts[1:] if re.fullmatch(r'[a-zA-Z0-9:_-]+', p)))

paths = ['/etc/apt/sources.list', '/etc/apt/apt-mirrors.txt']
paths += glob.glob('/etc/apt/sources.list.d/*.list')
paths += glob.glob('/etc/apt/sources.list.d/*.sources')
hosts = set()
for name in paths:
    path = pathlib.Path(name)
    if path.is_file():
        for value in re.findall(r'(?:https?|mirror\+https?)://[^\s]+', path.read_text()):
            host = urllib.parse.urlsplit(value.removeprefix('mirror+')).hostname
            if host:
                hosts.add(host)
for host in sorted(hosts):
    if any(host == domain or host.endswith('.' + domain) for domain in ('ubuntu.com', 'microsoft.com', 'github.com')):
        print('APT source host:', host)
    else:
        print('APT source host: [non-allowlisted host omitted]')
PY
else
  echo 'python3 unavailable; OS/resolver/APT source inspection skipped'
fi

for tool in getent dig curl timeout mysql mysqladmin mysqldump; do
  if command -v "$tool" >/dev/null 2>&1; then
    printf 'Tool available: %s\n' "$tool"
  else
    printf 'Tool unavailable: %s\n' "$tool"
  fi
done

if ! command -v timeout >/dev/null 2>&1; then
  echo 'timeout unavailable; network probes skipped to preserve the diagnostic time bound'
  exit 0
fi

for host in azure.archive.ubuntu.com archive.ubuntu.com security.ubuntu.com github.com packages.microsoft.com; do
  printf '\n::group::Network diagnosis for %s\n' "$host"
  if command -v getent >/dev/null 2>&1; then
    timeout 6s getent ahostsv4 "$host"
    printf 'System resolver exit status: %s\n' "$?"
  fi

  alternate_ip=''
  if command -v dig >/dev/null 2>&1; then
    for resolver in system 1.1.1.1 8.8.8.8; do
      printf 'DNS resolver: %s\n' "$resolver"
      if [ "$resolver" = system ]; then
        answer=$(timeout 5s dig -r "$host" A +time=2 +tries=1 +noall +comments +answer +stats 2>&1)
        result=$?
      else
        answer=$(timeout 5s dig -r "@$resolver" "$host" A +time=2 +tries=1 +noall +comments +answer +stats 2>&1)
        result=$?
      fi
      printf '%s\nDNS command exit status: %s\n' "$answer" "$result"
      if [ "$resolver" != system ] && [ -z "$alternate_ip" ]; then
        alternate_ip=$(printf '%s\n' "$answer" | awk '$4 == "A" && $5 ~ /^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$/ {print $5; exit}')
      fi
    done
  else
    echo 'dig unavailable; independent DNS comparison skipped'
  fi

  if command -v curl >/dev/null 2>&1; then
    if [[ "$host" == *.ubuntu.com ]]; then
      timeout 8s curl -q --silent --show-error --output /dev/null \
        --connect-timeout 3 --max-time 6 \
        --write-out 'HTTP code=%{http_code} remote_ip=%{remote_ip} dns_seconds=%{time_namelookup} connect_seconds=%{time_connect}\n' \
        "http://$host/"
      printf 'Default HTTP exit status: %s\n' "$?"
    fi
    timeout 10s curl -q --silent --show-error --output /dev/null \
      --connect-timeout 3 --max-time 8 \
      --write-out 'HTTPS code=%{http_code} remote_ip=%{remote_ip} dns_seconds=%{time_namelookup} connect_seconds=%{time_connect}\n' \
      "https://$host/"
    result=$?
    printf 'Default HTTPS exit status: %s\n' "$result"
    if [ "$result" -ne 0 ] && [ -n "$alternate_ip" ]; then
      printf 'HTTPS comparison using independent DNS address: %s\n' "$alternate_ip"
      timeout 10s curl -q --silent --show-error --output /dev/null \
        --connect-timeout 3 --max-time 8 --resolve "$host:443:$alternate_ip" \
        --write-out 'Resolved HTTPS code=%{http_code} remote_ip=%{remote_ip} connect_seconds=%{time_connect}\n' \
        "https://$host/"
      printf 'Resolved HTTPS exit status: %s\n' "$?"
    fi
  fi
  echo '::endgroup::'
done

exit 0
