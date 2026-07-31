# Lounge 조회 API k6 부하 테스트 가이드

## 1. 목적

라운지 조회 API의 응답시간, 실패율, 처리량을 측정하고 쿼리 병목 여부를 확인한다.

측정 결과를 기준으로 Projection, 인덱스, 캐시 적용 여부를 판단한다.

## 2. 대상 API

- 게시글 첫 페이지 조회
- 게시글 커서 조회
- 카테고리 및 커서 조회
- 게시글 상세 조회
- 댓글 목록 조회
- 답글 목록 조회

비로그인 요청과 로그인 요청을 각각 측정한다.

## 3. 테스트 환경

- 애플리케이션: `http://localhost:8080`
- DB: 로컬 Docker MySQL 8.4
- 게시글: 50,000건
- 게시글 이미지: 약 75,000건
- 루트 댓글: 약 125,000건
- 답글: 약 125,000건
- 게시글 좋아요: 약 75,000건
- 게시글 스크랩: 약 25,000건
- 댓글 좋아요: 약 375,000건

로컬 환경의 다른 작업이 측정 결과에 영향을 줄 수 있으므로 테스트 중에는 서버와 DB를 재시작하거나 무거운 작업을 실행하지 않는다.

## 4. 준비

k6를 설치한다.

```bash
brew install k6
```

상세, 댓글, 답글 조회에 사용할 ID를 확인한다.

```sql
SELECT
    post.loungePostId,
    parent.loungeCommentId AS parentCommentId
FROM LoungePost post
JOIN LoungeComment parent
    ON parent.loungePostId = post.loungePostId
   AND parent.parentCommentId IS NULL
WHERE post.title = 'DU102-PERF-50000'
  AND EXISTS (
      SELECT 1
      FROM LoungeComment reply
      WHERE reply.parentCommentId = parent.loungeCommentId
  )
LIMIT 1;
```

## 5. 테스트 프로파일

| 프로파일 | 설명 |
|---|---|
| `smoke` | VU 1명으로 30초 동안 API와 스크립트 정상 동작 확인 |
| `baseline` | 최대 VU 30명으로 일반 부하 측정 |
| `stress` | 최대 VU 150명으로 높은 부하 측정 |

## 6. 비로그인 테스트

Smoke:

```bash
BASE_URL=http://localhost:8080 \
TEST_PROFILE=smoke \
LOUNGE_POST_ID={게시글 ID} \
PARENT_COMMENT_ID={부모 댓글 ID} \
k6 run docs/lounge-query-load-test.k6.js
```

Baseline:

```bash
BASE_URL=http://localhost:8080 \
TEST_PROFILE=baseline \
LOUNGE_POST_ID={게시글 ID} \
PARENT_COMMENT_ID={부모 댓글 ID} \
k6 run docs/lounge-query-load-test.k6.js
```

Stress:

```bash
BASE_URL=http://localhost:8080 \
TEST_PROFILE=stress \
LOUNGE_POST_ID={게시글 ID} \
PARENT_COMMENT_ID={부모 댓글 ID} \
k6 run docs/lounge-query-load-test.k6.js
```

## 7. 로그인 테스트

Access Token을 터미널 환경변수로 저장한다.

```bash
read -s "ACCESS_TOKEN?Access Token 입력: "
export ACCESS_TOKEN
echo
```

토큰 값은 스크립트, 문서, Git에 기록하지 않는다.

테스트 명령에 다음 값을 추가한다.

```bash
ACCESS_TOKEN="$ACCESS_TOKEN"
```

로그인 baseline 예시:

```bash
BASE_URL=http://localhost:8080 \
TEST_PROFILE=baseline \
LOUNGE_POST_ID={게시글 ID} \
PARENT_COMMENT_ID={부모 댓글 ID} \
ACCESS_TOKEN="$ACCESS_TOKEN" \
k6 run docs/lounge-query-load-test.k6.js
```

## 8. 판단 기준

| 항목 | 기준 |
|---|---|
| Checks | 100% |
| 실패율 | 1% 미만 |
| p95 | 500ms 미만 |
| p99 | 1초 미만 |

## 9. 측정 결과

| 구분 | 전체 p95 | 전체 p99 | 실패율 | 처리량 |
|---|---:|---:|---:|---:|
| 비로그인 baseline | 23.78ms | 29.96ms | 0% | 84.40 req/s |
| 로그인 baseline | 31.76ms | 42.35ms | 0% | 82.14 req/s |
| 비로그인 stress | 40.39ms | 57.24ms | 0% | 373.53 req/s |
| 로그인 stress | 59.32ms | 84.69ms | 0% | 364.56 req/s |

로그인 stress의 API별 p95:

| API | p95 |
|---|---:|
| 게시글 첫 페이지 | 59.21ms |
| 게시글 커서 | 65.85ms |
| 카테고리 및 커서 | 64.31ms |
| 게시글 상세 | 58.67ms |
| 댓글 목록 | 54.11ms |
| 답글 목록 | 47.19ms |

## 10. 실행계획 확인 결과

게시글 첫 페이지 조회:

- PRIMARY KEY 역방향 스캔
- 실제 조회 행 51건
- 실제 실행시간 약 0.58ms

카테고리 및 커서 조회:

- PRIMARY KEY 역방향 range scan
- 실제 조회 행 204건
- 실제 실행시간 약 0.17ms

댓글 수 집계:

- `IDX_LOUNGECOMMENT_ROOT_CURSOR` covering index 사용
- 실제 조회 행 249건
- 실제 실행시간 약 0.11ms

## 11. 결론

최대 VU 150명의 로그인 stress 테스트에서도 모든 threshold를 통과했고 실패가 발생하지 않았다.

실행계획에서도 테이블 전체 스캔이나 과도한 조회 행이 확인되지 않았다. 따라서 현재 측정 결과만으로는 다음 변경을 적용하지 않는다.

- 조회 Projection 전환
- 추가 복합 인덱스
- Caffeine 캐시
- 카운트 컬럼 비정규화

향후 운영 환경에서 응답시간 증가, DB CPU 상승, slow query가 확인되면 동일한 스크립트로 다시 측정한 뒤 병목 지점만 개선한다.