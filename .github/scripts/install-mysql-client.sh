#!/usr/bin/env bash
set -euo pipefail

hosts=(azure.archive.ubuntu.com archive.ubuntu.com security.ubuntu.com)

echo '::group::Resolver state before temporary APT DNS override'
timeout 10s resolvectl status || true
timeout 10s journalctl --unit=systemd-resolved --lines=40 --no-pager || true
echo '::endgroup::'

work_dir=$(mktemp -d)
hosts_modified=false
cleanup() {
  local result=$?
  trap - EXIT
  if [ "$hosts_modified" = true ]; then
    if ! cat "$work_dir/hosts.original" > /etc/hosts; then
      echo "::error::Could not restore /etc/hosts; backup retained at $work_dir/hosts.original" >&2
      exit 1
    fi
    echo 'Restored original /etc/hosts'
  fi
  rm -rf "$work_dir"
  exit "$result"
}
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

for host in "${hosts[@]}"; do
  timeout 8s dig -r @1.1.1.1 "$host" A +time=2 +tries=1 +noall +comments +answer > "$work_dir/answer"
  python3 - "$host" "$work_dir/answer" >> "$work_dir/mappings" <<'PY'
import ipaddress
import pathlib
import sys

host, answer_file = sys.argv[1:]
answer = pathlib.Path(answer_file).read_text()
if 'status: NOERROR,' not in answer:
    sys.exit(f'DNS lookup failed for {host}')
addresses = []
for line in answer.splitlines():
    parts = line.split()
    if len(parts) == 5 and parts[2:4] == ['IN', 'A']:
        address = str(ipaddress.IPv4Address(parts[4]))
        if address not in addresses:
            addresses.append(address)
if not addresses:
    sys.exit(f'No IPv4 DNS answer for {host}')
for address in addresses:
    print(address, host)
PY
done

cp /etc/hosts "$work_dir/hosts.original"
hosts_modified=true
cat "$work_dir/mappings" "$work_dir/hosts.original" > /etc/hosts
printf 'Temporary APT DNS mappings from 1.1.1.1:\n'
cat "$work_dir/mappings"
for host in "${hosts[@]}"; do
  timeout 8s getent ahostsv4 "$host"
done

apt-get update
apt-get install -y default-mysql-client
