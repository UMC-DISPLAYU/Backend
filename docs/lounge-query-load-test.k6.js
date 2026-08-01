import http from 'k6/http';
import { check, group, sleep } from 'k6';
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN;
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TEST_PROFILE = __ENV.TEST_PROFILE || 'smoke';
const LOUNGE_POST_ID = __ENV.LOUNGE_POST_ID;
const PARENT_COMMENT_ID = __ENV.PARENT_COMMENT_ID;

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

const profile = PROFILES[TEST_PROFILE];

if (!profile) {
    throw new Error(`Unknown TEST_PROFILE: ${TEST_PROFILE}`);
}

if (!LOUNGE_POST_ID || !PARENT_COMMENT_ID) {
    throw new Error('LOUNGE_POST_ID and PARENT_COMMENT_ID are required');
}

export const options = {
    ...profile,
    thresholds: {
        checks: ['rate==1'],
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<500', 'p(99)<1000'],

        'http_req_failed{api:lounge_posts_first}': ['rate<0.01'],
        'http_req_duration{api:lounge_posts_first}': [
            'p(95)<500',
            'p(99)<1000',
        ],

        'http_req_failed{api:lounge_posts_cursor}': ['rate<0.01'],
        'http_req_duration{api:lounge_posts_cursor}': [
            'p(95)<500',
            'p(99)<1000',
        ],

        'http_req_failed{api:lounge_posts_category_cursor}': ['rate<0.01'],
        'http_req_duration{api:lounge_posts_category_cursor}': [
            'p(95)<500',
            'p(99)<1000',
        ],

        'http_req_failed{api:lounge_post_detail}': ['rate<0.01'],
        'http_req_duration{api:lounge_post_detail}': [
            'p(95)<500',
            'p(99)<1000',
        ],

        'http_req_failed{api:lounge_comments}': ['rate<0.01'],
        'http_req_duration{api:lounge_comments}': [
            'p(95)<500',
            'p(99)<1000',
        ],

        'http_req_failed{api:lounge_replies}': ['rate<0.01'],
        'http_req_duration{api:lounge_replies}': [
            'p(95)<500',
            'p(99)<1000',
        ],
    },
};

const RESPONSE_VALIDATORS = {
    lounge_posts_first: (data) => Array.isArray(data.posts),
    lounge_posts_cursor: (data) => Array.isArray(data.posts),
    lounge_posts_category_cursor: (data) => Array.isArray(data.posts),
    lounge_post_detail: (data) =>
        data.loungePostId === Number(LOUNGE_POST_ID),
    lounge_comments: (data) =>
        Array.isArray(data.comments) &&
        data.comments.every((comment) => Array.isArray(comment.imageUrls)),
    lounge_replies: (data) =>
        Array.isArray(data.replies) &&
        data.replies.every((reply) => Array.isArray(reply.imageUrls)),
};

function get(path, name) {
    const response = http.get(`${BASE_URL}${path}`, {
        headers: ACCESS_TOKEN
            ? {
                Accept: 'application/json',
                Authorization: `Bearer ${ACCESS_TOKEN}`,
            }
            : {
                Accept: 'application/json',
            },
        tags: { api: name },
    });

    let body;
    try {
        body = JSON.parse(response.body);
    } catch (_) {
        body = null;
    }

    check(response, {
        [`${name} status is 2xx`]: (result) =>
            result.status >= 200 && result.status < 300,
        [`${name} has expected response data`]: () => {
            const data = body && body.success && body.success.data;
            return Boolean(data && RESPONSE_VALIDATORS[name](data));
        },
    });
}

export default function () {
    group('lounge-posts', () => {
        get(
            '/api/v1/lounge/posts?size=50',
            'lounge_posts_first',
        );

        get(
            '/api/v1/lounge/posts?cursorId=25000&size=50',
            'lounge_posts_cursor',
        );

        get(
            '/api/v1/lounge/posts?category=WORK_TIP&cursorId=25000&size=50',
            'lounge_posts_category_cursor',
        );

        get(
            `/api/v1/lounge/posts/${LOUNGE_POST_ID}`,
            'lounge_post_detail',
        );
    });

    group('lounge-comments', () => {
        get(
            `/api/v1/lounge/posts/${LOUNGE_POST_ID}/comments?size=50`,
            'lounge_comments',
        );

        get(
            `/api/v1/lounge/comments/${PARENT_COMMENT_ID}/replies?size=50`,
            'lounge_replies',
        );
    });

    sleep(1);
}
