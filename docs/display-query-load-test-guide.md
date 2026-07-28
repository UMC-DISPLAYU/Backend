# Display 조회 API k6 부하 테스트 가이드

목적: QueryDSL 적용 전/후 Display 조회 API의 응답 시간, 실패율, 처리량을 같은 조건에서 비교한다.

대상 API:

- `/api/v1/display/search`
- `/api/v1/display/map`
- `/api/v1/display/closing-soon`
- `/api/v1/display/graduation`
- `/api/v1/display/du-picks`

## 1. 준비

k6 설치:

```bash
brew install k6
```

테스트 전 확인:

- 같은 DB 데이터를 사용한다.
- 비교 대상 서버를 각각 다른 포트로 실행한다.
- 첫 실행 결과는 JVM/JPA/DB cache warm-up 영향이 있으므로 버린다.
- baseline과 querydsl 순서를 바꿔 2회 이상 반복한다.

권장 포트:

| 대상 | 예시 URL |
|---|---|
| 수정 이전 baseline | `http://localhost:8081` |
| QueryDSL 적용 이후 | `http://localhost:8082` |

## 2. 실행 방법

Smoke 테스트:

```bash
BASE_URL=http://localhost:8082 TEST_PROFILE=smoke k6 run docs/display-query-load-test.k6.js
```

기본 비교 테스트:

```bash
BASE_URL=http://localhost:8081 TEST_PROFILE=baseline k6 run docs/display-query-load-test.k6.js
BASE_URL=http://localhost:8082 TEST_PROFILE=baseline k6 run docs/display-query-load-test.k6.js
```

Stress 테스트:

```bash
BASE_URL=http://localhost:8082 TEST_PROFILE=stress k6 run docs/display-query-load-test.k6.js
```

결과를 파일로 저장:

```bash
BASE_URL=http://localhost:8082 TEST_PROFILE=baseline \
  k6 run --summary-export docs/display-query-load-test-result.json \
  docs/display-query-load-test.k6.js
```

## 3. 측정값 확인 방법

k6 콘솔에서 우선 볼 값:

| Metric | 의미 | 판단 기준 |
|---|---|---|
| `http_reqs` | 총 요청 수 | 동일 profile에서 전/후 처리량 비교 |
| `http_req_duration avg` | 평균 응답 시간 | 낮을수록 좋음 |
| `http_req_duration p(95)` | 95% 요청 응답 시간 | MVP 기준 500ms 미만 권장 |
| `http_req_duration p(99)` | 99% 요청 응답 시간 | MVP 기준 1s 미만 권장 |
| `http_req_failed` | 실패율 | 1% 미만 유지 |
| `iterations` | 전체 시나리오 반복 수 | 동일 조건에서 높을수록 처리량 좋음 |
| `checks` | status/body 검증 성공률 | 100% 기대 |

API별 SLO는 k6 threshold의 `api` tag 기준으로 판단한다. 각 API의 `http_req_duration{api:<displayName>}` P95는 500ms 미만, P99는 1s 미만이어야 하고 `http_req_failed{api:<displayName>}` 실패율은 1% 미만이어야 한다. 콘솔 기본 요약이 부족하면 Prometheus/Grafana, InfluxDB, 또는 k6 Cloud로 전송해서 `api=display_map_default` 같은 tag별 metric을 본다.

간단 비교 표:

| 항목 | baseline | querydsl | 판단 |
|---|---:|---:|---|
| 전체 avg | k6 출력값 | k6 출력값 | 낮은 쪽 우세 |
| 전체 p95 | k6 출력값 | k6 출력값 | 500ms 미만 여부 |
| 전체 p99 | k6 출력값 | k6 출력값 | 1s 미만 여부 |
| 실패율 | k6 출력값 | k6 출력값 | 1% 미만 여부 |
| iterations | k6 출력값 | k6 출력값 | 높은 쪽 우세 |

## 4. DB 측정값 확인

부하 테스트와 함께 DB도 확인해야 한다. QueryDSL 전환은 SQL 구조가 유사하면 k6 결과 차이가 작을 수 있으므로 DB 실행 계획이 중요하다.

MySQL에서 확인할 값:

```sql
SHOW FULL PROCESSLIST;
SHOW GLOBAL STATUS LIKE 'Threads_connected';
SHOW GLOBAL STATUS LIKE 'Threads_running';
SHOW GLOBAL STATUS LIKE 'Slow_queries';
```

slow query log가 켜져 있다면 테스트 중 발생한 쿼리를 확인한다.

주요 쿼리는 별도로 `EXPLAIN ANALYZE`를 실행한다:

```sql
EXPLAIN ANALYZE
SELECT ...
```

DB 병목 판단 기준:

| 항목 | 병목 의심 기준 |
|---|---|
| `Rows examined` | 페이지 크기 대비 과도하게 큼 |
| `Using filesort` | 정렬 인덱스 미사용 가능성 |
| `Using temporary` | group/order/join 비용 증가 가능성 |
| DB CPU | 70% 이상 지속 |
| Slow query | Display 조회 API에서 반복 발생 |

## 5. Spring Actuator 확인

Actuator가 열려 있으면 부하 중 아래 값을 확인한다.

```bash
curl http://localhost:8082/actuator/metrics/http.server.requests
curl http://localhost:8082/actuator/metrics/jvm.memory.used
curl http://localhost:8082/actuator/metrics/hikaricp.connections.active
curl http://localhost:8082/actuator/metrics/hikaricp.connections.pending
```

중요한 값:

| Metric | 확인 이유 |
|---|---|
| `http.server.requests` | API별 서버 응답 시간 |
| `jvm.memory.used` | 메모리 증가 여부 |
| `hikaricp.connections.active` | DB 커넥션 사용량 |
| `hikaricp.connections.pending` | 커넥션 대기 발생 여부 |

## 6. 해석 기준

이번 QueryDSL 적용은 기존 JPQL을 타입 안전한 동적 쿼리로 옮긴 변경이다. SQL 구조가 거의 같다면 성능 차이는 오차 범위일 수 있다.

유의미한 개선으로 볼 수 있는 기준:

- 동일 profile에서 P95가 10% 이상 감소
- 실패율이 낮아짐
- `iterations`가 10% 이상 증가
- DB slow query 또는 rows examined가 감소

차이가 작다면 다음 최적화 대상은 QueryDSL 자체가 아니라 다음 항목이다.

1. `Display`, `DisplayImage`, `DisplayField` 복합 인덱스 추가
2. 대표 이미지 subquery 구조 개선
3. `/display/graduation`의 `order by rand()` 제거
4. `/display/map` 지도 범위 조회 인덱스 검증
