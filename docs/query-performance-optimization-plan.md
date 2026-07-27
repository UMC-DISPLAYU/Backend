# DisplayU 조회 성능 최적화 실행 계획

작성 기준: 현재 백엔드 코드베이스 정적 분석 기준  
범위: 구현 없이 조회 성능 최적화를 위한 단계별 계획 수립

## 1. 현재 상태 요약

| 항목 | 현재 상태 | 개선 포인트 |
|---|---|---|
| QueryDSL | `build.gradle`에 QueryDSL 의존성 없음. 현재 조회는 Spring Data JPA `@Query` + JPQL DTO Projection 중심 | QueryDSL 도입 시 현재 JPQL 조회 API부터 Query Repository로 전환 |
| Projection DTO | 전시 검색/지도/마감임박/두픽은 DTO Projection 사용 | 커뮤니티/아카이브/작품 일부는 Entity 조회 후 Result 변환 |
| N+1 후보 | 전시 상세, 작품 목록/상세, 후기 이미지, Aggregate 컬렉션 순회 | 상세 조회 전용 Query DTO 또는 fetch join 분리 |
| 불필요 Join | 전시 목록 쿼리마다 대표 이미지 `left join + min(sortOrder)` 반복 | 대표 이미지 조회 전략 단순화 또는 인덱스 보강 |
| Cursor Pagination | 검색/지도/마감임박/커뮤니티/라운지/아카이브 다수 적용 | 아카이브 서브쿼리 커서, 검색 cursor 방향/정렬 일관성 점검 |
| DB Index | 라운지/후기/일부 커뮤니케이션 인덱스 존재 | `Display`, `DisplayImage`, `DisplayField` 지도/검색용 인덱스 보강 필요 |
| Cache/Redis/Kafka | Caffeine/Redis/Kafka 의존성 없음 | MVP는 Caffeine 우선, Redis/Kafka는 성장 단계 도입 |

주요 확인 파일:

- `src/main/java/com/example/demo/domain/display/infrastructure/persistence/SpringDataSearchDisplayQueryJpaRepository.java`
- `src/main/java/com/example/demo/domain/display/infrastructure/persistence/SpringDataDisplayMapQueryJpaRepository.java`
- `src/main/java/com/example/demo/domain/display/infrastructure/persistence/SpringDataClosingSoonDisplayQueryJpaRepository.java`
- `src/main/java/com/example/demo/domain/display/application/query/GetDisplayDetailService.java`
- `src/main/java/com/example/demo/domain/display/application/result/DisplayDetailResult.java`
- `build.gradle`

## 2. QueryDSL 조회 성능 분석 계획

현재는 QueryDSL 구현체가 없으므로, 먼저 기존 JPQL 조회를 기준으로 성능 병목을 확인한 뒤 QueryDSL 전환 여부를 결정한다.

| 단계 | 목적 | 적용 대상 API | 기대 효과 | 우선순위 | 적용 시점 |
|---|---|---|---|---|---|
| 1 | 조회 쿼리 감사 | `/api/v1/display/search`, `/api/v1/display/map`, `/api/v1/display/closing-soon`, `/api/v1/display/graduation`, `/api/v1/display/{displayId}` | 실제 병목 API 확정 | P0 | 즉시 |
| 2 | Projection DTO 적용 확대 | 전시 상세, 작품 목록/상세, 후기/감상/질문 목록, 아카이브 목록 | Entity 로딩, 영속성 컨텍스트 비용, Lazy 로딩 감소 | P0 | 1단계 직후 |
| 3 | N+1 제거 | 전시 상세의 images/categories/teamMembers/invitations, 작품 images/creator/display, 후기 images | API당 쿼리 수 안정화 | P0 | Projection 전환과 함께 |
| 4 | 대표 이미지 조회 최적화 | 검색/지도/마감임박/졸업전시 | correlated subquery 반복 비용 감소 | P0 | 인덱스 설계 전 |
| 5 | 불필요 Join 정리 | 전시 검색/지도/졸업전시 | Row explosion 방지, 실행계획 단순화 | P1 | 1차 부하 테스트 전 |
| 6 | Cursor Pagination 보정 | 검색, 지도, 아카이브 | 대량 데이터에서 안정적 페이지 이동 | P1 | MVP 데이터 증가 전 |
| 7 | Fetch 전략 점검 | `DisplayRepository.findById`, 작품/개인작품 상세 | Command용 Aggregate와 Query용 DTO 경계 명확화 | P1 | 상세 API 최적화 시 |

### API별 점검 포인트

| API | 현재 구조 | 점검 내용 |
|---|---|---|
| `/api/v1/display/search` | JPQL DTO Projection, `d.id > cursor`, 대표 이미지 subquery, optional filter | `status + id`, `region/type/date`, `field exists`, `%keyword%` 검색 비용 |
| `/api/v1/display/map` | JPQL DTO Projection, latitude/longitude range, `d.id < cursor`, 대표 이미지 subquery | 지도 bbox 인덱스, 검색어 포함 시 full scan 가능성 |
| `/api/v1/display/closing-soon` | `endDate asc, id asc` 복합 커서 | `(status, endDate, id)` 인덱스 필요 |
| `/api/v1/display/graduation` | `order by rand()` | 데이터 증가 시 최우선 제거 대상 |
| `/api/v1/display/{displayId}` | Aggregate 조회 후 여러 컬렉션 순회 | Lazy loading/N+1 확인, 상세 전용 Query DTO 검토 |
| `/api/v1/artworks`, `/api/v1/artworks/preview` | Entity 조회 후 image/display/creator 조합 | 작품 카드 전용 Projection 검토 |
| 후기/감상/질문 목록 | Cursor + 배치 count 일부 적용 | Entity 대신 목록 전용 Projection 검토 |
| 아카이브 목록 | Entity 조회 + memo 배치 조회 | 커서 기준값 서브쿼리 제거 검토 |

## 3. DB 성능 최적화 계획

| 단계 | 목적 | 적용 대상 API | 기대 효과 | 우선순위 | 적용 시점 |
|---|---|---|---|---|---|
| 1 | `EXPLAIN ANALYZE` 대상 선정 | 전시 검색, 지도, 마감임박, 졸업 랜덤, 전시 상세, 작품 목록 | 추측 없는 인덱스 설계 | P0 | 즉시 |
| 2 | `Display` 복합 인덱스 설계 | `/display/search`, `/display/closing-soon`, `/display/graduation` | Full scan 감소 | P0 | EXPLAIN 후 |
| 3 | 지도 조회 인덱스 설계 | `/display/map` | 지도 바운딩 박스 조회 속도 개선 | P0 | 지도 API 부하 전 |
| 4 | `DisplayImage` 인덱스 설계 | 전시 목록 계열 전체 | 대표 이미지 join/subquery 비용 감소 | P0 | 목록 쿼리 개선과 함께 |
| 5 | `DisplayField` 인덱스 검토 | `/display/search?field=` | 분야 필터 성능 개선 | P1 | 검색 필터 사용량 증가 시 |
| 6 | Covering Index 적용 가능성 검토 | 검색/지도/마감임박 | 테이블 접근 감소 | P2 | 데이터 10만 건 이상 |
| 7 | 검색어 처리 개선 | title/placeName 검색 | `%keyword%` 검색 한계 보완 | P2 | 검색 트래픽 증가 시 |

### 우선 검토할 인덱스 후보

| 테이블 | 후보 인덱스 | 적용 대상 |
|---|---|---|
| `Display` | `(status, displayId)` | 검색 기본 커서 |
| `Display` | `(status, endDate, displayId)` | 마감임박 |
| `Display` | `(status, displayType, displayId)` | 졸업전시/유형 필터 |
| `Display` | `(status, latitude, longitude, displayId)` | 지도 조회 |
| `DisplayImage` | `(displayId, imageType, deletedAt, sortOrder)` | 대표 이미지 조회 |
| `DisplayField` | `(field, displayId)` 또는 기존 `(displayId, field)` 재검토 | 분야 필터 |
| `DisplayArtwork` | `(displayId, deletedAt, workSortOrder)` | 전시별 작품 목록 |
| `DisplayArtwork` | `(deletedAt, createdAt, displayId)` | 작품 프리뷰 |
| `ArchiveDisplay`, `ArchiveWork`, `ArchiveArtist` | `(userId, savedAt, id)` | 아카이브 커서 |
| `PersonalArtwork` | 기존 `(userId, createdAt)`에 `deletedAt`, `id` 포함 검토 | 개인 작품 목록 |

### EXPLAIN ANALYZE 우선순위

1. `/api/v1/display/map`
2. `/api/v1/display/search`
3. `/api/v1/display/closing-soon`
4. `/api/v1/display/graduation`
5. `/api/v1/display/{displayId}`
6. `/api/v1/artworks/preview`
7. 라운지/후기/작품 감상 목록

## 4. 부하 테스트 계획

| 항목 | 계획 |
|---|---|
| 가장 먼저 테스트할 API | 1순위 `/api/v1/display/map`, 2순위 `/api/v1/display/search`, 3순위 `/api/v1/display/{displayId}`, 4순위 `/api/v1/artworks/preview`, 5순위 라운지/후기 목록 |
| 예상 TPS | MVP 기준 read API 30~100 TPS, 지도/검색 피크 100 TPS 가정 |
| 성장 단계 TPS | 300~500 TPS에서 재측정 |
| 측정 Metric | 평균/최대 Response Time, P95/P99, Error Rate, JVM CPU/Heap/GC, DB CPU, Slow Query, Rows Examined, Buffer Pool Hit, Connection Pool 대기 |
| 병목 판단 기준 | P95 500ms 초과, P99 1s 초과, DB CPU 70% 이상 지속, Rows Examined/Page 1,000 이상, 커넥션 대기 발생, GC pause 증가 |
| 도구 | k6 또는 Gatling, MySQL `EXPLAIN ANALYZE`, slow query log, Spring Actuator |

## 5. Local Cache(Caffeine) 도입 계획

MVP 단계에서는 단일 서버 기준으로 Caffeine이 Redis보다 단순하고 비용이 낮다. 캐시 정합성이 강하게 필요하지 않고, 짧은 TTL로 감당 가능한 조회부터 적용한다.

| 캐싱 대상 API | TTL 전략 | Eviction 전략 | Invalidation 전략 | 우선순위 | 적용 시점 |
|---|---|---|---|---|---|
| `/api/v1/display/du-picks` | 10~30분 | `maximumSize` 100~500 | 두픽 컬럼 변경 시 수동 evict | P0 | 즉시 가능 |
| `/api/v1/display/graduation` 후보 목록 | 5~10분 | `maximumSize` 100 | 전시 발행/수정 시 evict | P0 | `order by rand()` 대체 시 |
| `/api/v1/display/closing-soon` 첫 페이지 | 1~5분 | `maximumSize` 100~300 | 전시 발행/기간 수정 시 evict | P1 | 부하 테스트 후 |
| `/api/v1/agreements` | 1시간 이상 | small fixed size | 약관 변경 시 evict | P1 | 상시 |
| `/api/v1/display/map`, `/api/v1/display/search` | 30초~2분 | bounds/search key 기반, size 제한 | 전시 발행/수정 시 전체 evict 가능 | P2 | 트래픽 패턴 확인 후 |

## 6. Redis 도입 계획

| 영역 | Caffeine으로 충분한 경우 | Redis가 필요한 경우 |
|---|---|---|
| JWT | Access Token 자체 검증만 하는 경우 | 로그아웃 토큰 무효화, Refresh Token 저장, 다중 서버 세션 관리 |
| Email Verification | 단일 서버, DB 기반 인증코드 저장으로 충분한 경우 | 인증코드 TTL, 시도횟수, 재전송 쿨다운을 DB 부하 없이 관리 |
| Rate Limiting | 단일 서버 기준 임시 제한 | 다중 서버에서 IP/User 기준 전역 제한 |
| Shared Cache | 단일 서버 | 서버 2대 이상 Scale-out |
| Ranking | 부적합 | 좋아요/조회수 기반 실시간 랭킹, Sorted Set 필요 |
| 검색/지도 캐시 | 짧은 TTL의 단일 서버 캐시 | 캐시 공유와 명시 무효화가 중요해질 때 |

도입 시점:

1. 서버가 2대 이상으로 늘어난다.
2. JWT blacklist 또는 Refresh Token 서버 저장이 필요하다.
3. 이메일 인증/Rate Limit이 DB write 부하를 만든다.
4. 랭킹, 인기 전시, 인기 작품처럼 실시간 집계성 조회가 생긴다.

## 7. Kafka 도입 계획

| 항목 | 판단 |
|---|---|
| 현재 MVP 필요 여부 | 불필요. 현재 기능은 동기 DB 트랜잭션과 단순 메일 발송/조회 중심이라 Kafka 도입 비용이 큼 |
| 필요한 미래 기능 | 좋아요/조회수 이벤트 집계, 알림, 이메일 비동기 발송, 랭킹 갱신, 추천 피드, 감사 로그, 검색 색인 동기화 |
| 이벤트 기반 전환 고려사항 | Outbox Pattern, 이벤트 중복 처리, Consumer idempotency, DLQ, 재처리 정책, 트랜잭션 경계, 이벤트 스키마 버전 관리 |
| 권장 도입 순서 | Spring Event/TransactionalEventListener → 비동기 executor → Redis Stream 또는 Queue 검토 → Kafka |

## 8. 현재 MVP에서 가장 먼저 해야 하는 작업

| 순서 | 작업 | 이유 |
|---:|---|---|
| 1 | `/display/map`, `/display/search`, `/display/closing-soon` 실제 SQL `EXPLAIN ANALYZE` 수집 | 가장 사용자 노출이 크고 DB scan 가능성이 높음 |
| 2 | `Display`, `DisplayImage`, `DisplayField` 인덱스 설계 | 목록 조회 성능의 핵심 |
| 3 | 전시 상세 조회를 Query DTO로 분리할지 결정 | Aggregate 컬렉션 Lazy 로딩/N+1 가능성 제거 |
| 4 | 졸업전시 `order by rand()` 제거 계획 수립 | 데이터 증가 시 급격히 느려짐 |
| 5 | k6로 검색/지도 기준 부하 테스트 작성 | 개선 전후 수치 비교 기준 확보 |

## 9. 서비스 성장 기술 로드맵

| 단계 | 적용 기술/작업 | 적용 기준 |
|---|---|---|
| MVP | JPQL/QueryDSL 조회 쿼리 정리, DTO Projection, 복합 인덱스, EXPLAIN | 데이터 수천~수만 건, 단일 서버 |
| MVP+ | Caffeine Cache, slow query log, actuator metric, 부하 테스트 자동화 | 반복 조회 API P95가 흔들릴 때 |
| 초기 성장 | Redis for Email Verification, Rate Limit, JWT blacklist, Shared Cache | 서버 2대 이상 또는 인증/제한 정합성 필요 |
| 성장 | 검색 구조 개선, 지도 쿼리 고도화, 랭킹 Redis Sorted Set | 전시/작품 데이터 10만 건 이상 |
| 확장 | Kafka + Outbox, 비동기 알림/랭킹/색인 갱신 | 이벤트 처리량 증가, 동기 요청에서 분리 필요 |
| 고도화 | Read Model 분리, 검색 엔진, CDN/이미지 메타 캐시 | 검색/지도/피드가 핵심 트래픽이 될 때 |

## 10. 작업 원칙

- 먼저 측정하고, 그 다음 쿼리와 인덱스를 수정한다.
- Command용 Aggregate 조회와 Query용 DTO 조회를 분리한다.
- 단순 CRUD나 낮은 트래픽 API에는 과한 구조를 도입하지 않는다.
- Redis/Kafka는 MVP 성능 문제가 아니라 운영 규모 문제가 생겼을 때 도입한다.
- 모든 인덱스 추가는 `EXPLAIN ANALYZE` 전후 비교와 함께 Flyway migration으로 관리한다.
