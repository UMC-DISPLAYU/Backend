import http from 'k6/http';
import { check, group, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TEST_PROFILE = __ENV.TEST_PROFILE || 'smoke';

const PROFILES = {
  smoke: {
    vus: 1,
    duration: '30s',
  },
  baseline: {
    stages: [
      { duration: '30s', target: 10 },
      { duration: '1m', target: 30 },
      { duration: '30s', target: 0 },
    ],
  },
  stress: {
    stages: [
      { duration: '1m', target: 30 },
      { duration: '2m', target: 100 },
      { duration: '1m', target: 150 },
      { duration: '30s', target: 0 },
    ],
  },
};

export const options = {
  ...PROFILES[TEST_PROFILE],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    'http_req_failed{api:display_search_default}': ['rate<0.01'],
    'http_req_duration{api:display_search_default}': ['p(95)<500', 'p(99)<1000'],
    'http_req_failed{api:display_search_filtered}': ['rate<0.01'],
    'http_req_duration{api:display_search_filtered}': ['p(95)<500', 'p(99)<1000'],
    'http_req_failed{api:display_map_default}': ['rate<0.01'],
    'http_req_duration{api:display_map_default}': ['p(95)<500', 'p(99)<1000'],
    'http_req_failed{api:display_map_search}': ['rate<0.01'],
    'http_req_duration{api:display_map_search}': ['p(95)<500', 'p(99)<1000'],
    'http_req_failed{api:display_closing_soon}': ['rate<0.01'],
    'http_req_duration{api:display_closing_soon}': ['p(95)<500', 'p(99)<1000'],
    'http_req_failed{api:display_graduation}': ['rate<0.01'],
    'http_req_duration{api:display_graduation}': ['p(95)<500', 'p(99)<1000'],
    'http_req_failed{api:display_du_picks}': ['rate<0.01'],
    'http_req_duration{api:display_du_picks}': ['p(95)<500', 'p(99)<1000'],
  },
};

const DEFAULT_HEADERS = {
  headers: {
    Accept: 'application/json',
  },
};

function get(path, name) {
  const response = http.get(`${BASE_URL}${path}`, {
    ...DEFAULT_HEADERS,
    tags: { api: name },
  });

  check(response, {
    [`${name} status is 2xx`]: (r) => r.status >= 200 && r.status < 300,
    [`${name} has response body`]: (r) => r.body && r.body.length > 0,
  });

  return response;
}

export default function () {
  group('display-search', () => {
    get(
      '/api/v1/displays/search?cursor=0&size=20',
      'display_search_default',
    );
    get(
      '/api/v1/displays/search?searchWord=%EC%A0%84%EC%8B%9C&status=ONGOING&region=SEOUL&cursor=0&size=20',
      'display_search_filtered',
    );
  });

  group('display-map', () => {
    get(
      '/api/v1/displays/map?southLatitude=37.4500&westLongitude=126.8500&northLatitude=37.7000&eastLongitude=127.1500&size=50',
      'display_map_default',
    );
    get(
      '/api/v1/displays/map?southLatitude=37.4500&westLongitude=126.8500&northLatitude=37.7000&eastLongitude=127.1500&searchWord=%EC%84%9C%EC%9A%B8&size=50',
      'display_map_search',
    );
  });

  group('display-curation', () => {
    get('/api/v1/displays/closing-soon?size=20', 'display_closing_soon');
    get('/api/v1/displays/graduation?size=10', 'display_graduation');
    get('/api/v1/displays/du-picks?size=10', 'display_du_picks');
  });

  sleep(1);
}
