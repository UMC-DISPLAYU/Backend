import { check, fail, group, sleep } from 'k6';
import http from 'k6/http';
import { Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const VUS = Number(__ENV.VUS || 20);
const DURATION = __ENV.DURATION || '30s';
const SLEEP_SECONDS = Number(__ENV.SLEEP_SECONDS || 0.1);
const SIZE = Number(__ENV.SIZE || 20);

const graduationDuration = new Trend('display_graduation_duration');
const duPicksDuration = new Trend('display_du_picks_duration');
const closingSoonDuration = new Trend('display_closing_soon_duration');

const ENDPOINTS = {
  graduation: `${BASE_URL}/api/v1/display/graduation?size=${SIZE}`,
  duPicks: `${BASE_URL}/api/v1/display/du-picks?size=${SIZE}`,
  closingSoon: `${BASE_URL}/api/v1/display/closing-soon?size=${SIZE}`,
};

export const options = {
  discardResponseBodies: true,
  scenarios: {
    display_main_cached_reads: {
      executor: 'constant-vus',
      vus: VUS,
      duration: DURATION,
    },
  },
  summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    checks: ['rate>0.99'],
  },
};

export function setup() {
  const warmUpResponses = [
    ['graduation warm-up status is 200', http.get(ENDPOINTS.graduation)],
    ['du-picks warm-up status is 200', http.get(ENDPOINTS.duPicks)],
    ['closing-soon warm-up status is 200', http.get(ENDPOINTS.closingSoon)],
  ];

  for (const [name, res] of warmUpResponses) {
    const ok = check(res, {
      [name]: (r) => r.status === 200,
    });

    if (!ok) {
      fail(`${name} failed with status ${res.status}`);
    }
  }
}

export default function () {
  group('display graduation cached read', () => {
    const res = http.get(ENDPOINTS.graduation);
    graduationDuration.add(res.timings.duration);
    check(res, {
      'graduation status is 200': (r) => r.status === 200,
    });
  });

  group('display du-picks cached read', () => {
    const res = http.get(ENDPOINTS.duPicks);
    duPicksDuration.add(res.timings.duration);
    check(res, {
      'du-picks status is 200': (r) => r.status === 200,
    });
  });

  group('display closing-soon first page cached read', () => {
    const res = http.get(ENDPOINTS.closingSoon);
    closingSoonDuration.add(res.timings.duration);
    check(res, {
      'closing-soon status is 200': (r) => r.status === 200,
    });
  });

  sleep(SLEEP_SECONDS);
}
