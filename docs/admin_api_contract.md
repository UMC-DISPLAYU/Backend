# Admin 전시 심사 API 계약 초안

관련 이슈: [#544 / DU-230](https://github.com/UMC-DISPLAYU/Backend/issues/544)

## 작업 범위

Admin은 관리자 HTTP 요청과 전시 심사 흐름을 담당.
User 역할 저장·내 정보 응답과 Display 소유 코드는 각 담당자가 수정.
JWT 인증 정보에 역할을 연결하고 관리자 API 접근을 제한하는 공통 보안 변경은 Admin 담당 범위.
아래 API는 협의용 초안이며 아직 제공되는 엔드포인트가 아님.

## 계층별 책임

| 패키지 | 책임 |
| --- | --- |
| `admin.presentation` | Controller, 요청·응답 DTO, Presentation Mapper |
| `admin.application` | 관리자 권한 확인과 전시 심사 유스케이스 조율, Command/Result |
| `admin.domain` | Admin이 독립적으로 소유하는 규칙이 생길 때 추가 |
| `admin.infrastructure` | 합의된 외부 도메인 계약을 연결하는 어댑터가 필요할 때 추가 |

전시 Aggregate, 상태 전이 규칙, 반려 사유와 저장소는 Display 소유.
Admin 전용 전시 Entity나 테이블을 추가하지 않음.
타 도메인의 JPA Entity·Repository 구현체를 직접 참조하지 않음.
실제 구현이 필요한 시점에 하위 패키지를 추가하고 빈 계층이나 임시 구현체를 만들지 않음.

## API 초안

기존 `/api/v1` 및 `display` 명칭을 기준으로 제안. 경로·응답 구조는 담당자 합의 후 확정.

| Method | 경로 | 기능 | 입력 초안 |
| --- | --- | --- | --- |
| GET | `/api/v1/admin/displays` | 심사 목록 | `status=PENDING_REVIEW`, `cursor`, `size=20` |
| GET | `/api/v1/admin/displays/{displayId}` | 심사 상세 | 전시 ID |
| POST | `/api/v1/admin/displays/{displayId}/approve` | 승인 | 전시 ID |
| POST | `/api/v1/admin/displays/{displayId}/reject` | 반려 | 전시 ID, `reason` |

- 전체 API는 ADMIN만 접근. 미인증 401, 일반 사용자 403.
- 기존 공통 응답·예외 처리 방식을 사용. 성공은 HTTP 200, 승인·반려의 `success.data`는 null.
- 반려 사유는 필수이며 1,000자 이하로 제안. 공백만 입력하면 400, 유효한 사유는 양끝 공백 제거 후 전달.
- 상세 응답은 심사에 필요한 전시 정보, 심사 상태, 반려 사유를 포함하는 방향으로 협의.
- 존재하지 않는 전시, 심사 불가 상태, 중복 처리의 오류 정책은 Display 계약을 따름.

## 협업 계약

| 담당 | 필요한 계약 |
| --- | --- |
| User | USER/ADMIN 저장, 기존·신규 회원 기본 USER, `/users/me` role, 일반 회원 API의 역할 변경 차단, 최초 ADMIN 지정 방식 협의 |
| Admin | 기존 카카오·구글 로그인 재사용, JWT 인증 정보의 역할 연결, 관리자 API 접근 제한, 심사 API |
| Display | 심사 목록·상세 조회 유스케이스, 승인·반려 유스케이스, 반려 사유 저장 |

최신 dev 확인 시 `AuthUser`에는 `userId`만 존재하고 JWT 인증의 권한 목록은 비어 있음.
`DisplayStatus`는 `DRAFT/PUBLISHED`만 제공. 심사 상태를 Admin에서 별도로 정의하지 않음.

구현 전 합의할 사항:

- 심사 요청 진입점과 심사 대상 상태, 승인 후 발행 흐름, 반려 후 재심사 정책
- 조회 결과 필드, 페이지 방식, 유스케이스 입력·출력 타입
- 관리자 권한 제공 방식과 담당자의 변경 범위
- 상태 변경의 트랜잭션 경계 및 동시·중복 심사 처리

담당자에게 전달한 전시 협업 요청:

- `DRAFT → PENDING_REVIEW → PUBLISHED / REJECTED` 상태 제안
- 기존 등록 요청을 즉시 공개 대신 검토 대기로 변경
- 심사 요청일·처리일·처리자·반려 사유 저장 구조
- 검토 중 수정 제한, 반려 후 수정·재신청, 승인 후 주요 정보 수정 시 재심사 여부
- 미승인 전시의 일반 목록·검색·상세·연결 콘텐츠 공개 제한
- 승인 시 기존 전시·콘텐츠 공개 및 캐시 갱신 처리 재사용

위 항목은 협업 요청을 전달한 상태이며 확정된 계약이 아님.
User의 역할 필드명·응답 형식과 Display의 상태·저장 필드·호출 계약을 공유받은 뒤 연결.

권한·전시 연동 구현 없이 관리자 엔드포인트를 노출하거나 성공 응답을 반환하지 않음.

## 수정 파일 계획

Admin은 기존 도메인과 동일하게 `presentation/application/domain/infrastructure` 구조를 사용.
빈 계층 디렉터리는 로컬에서만 유지하고 실제 구현 파일이 추가될 때 Git에 반영.
`admin.presentation`의 Controller·DTO·Mapper와 `admin.application`의 심사 처리 코드를
초안 계약으로 구현하고 `src/test/java/com/example/demo/domain/admin`에서 mock 기반 검증.
연동 어댑터는 계약상 필요할 때만 추가. User·Display 코드는 직접 수정하지 않음.
역할 계약 확정 후 `global.security`의 JWT 인증 흐름과 `global.config.SecurityConfig`는
역할 연결·관리자 API 접근 제한에 필요한 범위에서 수정하고 권한 테스트 추가.

브랜치: `feat/DU-230`

커밋은 `docs: 내용`, `chore: 내용`, `feat: 내용` 형식으로 계약 문서, 초기 구성, 실제 기능별로 분리.
Java 코드 변경 후 `spotlessCheck`와 관련 검증 실행.


## 담당자에게 공유할 호출 계약

모두 Admin이 제안하는 소비자 측 인터페이스. 각 담당자는 자기 도메인의 공개 유스케이스를
제공하고, Admin의 어댑터가 이 계약에 연결하는 방향. 다른 도메인이 Admin 타입을 직접 사용할 필요는 없음.

- `AdminAccessPort.isAdmin(userId)`: 현재 운영자 여부. 미존재·탈퇴 사용자는 false.
- `DisplayReviewPort.search(ReviewSearchQuery)`: 목록과 다음 커서 반환.
- `DisplayReviewPort.getDetail(displayId)`: 미공개 심사 전시의 상세 반환.
- `DisplayReviewPort.approve(displayId, reviewerId)`: 승인·공개 처리.
- `DisplayReviewPort.reject(displayId, reviewerId, reason)`: 반려와 사유 저장.

처리자 ID는 인증 정보에서 가져오며 요청 본문에서 받지 않음.
Display는 승인·반려 가능 상태를 검증하고 상태·처리자·처리일·사유를 하나의 트랜잭션으로 처리.
동시에 승인·반려 요청이 들어와도 하나만 반영하고, 이미 처리된 건은 409로 응답하는 계약 제안.
미존재 전시는 404. 연동 예외는 기존 BusinessException 체계로 전달하며 Admin은 성공으로 바꾸지 않음.
승인 알림은 아직 구현하지 않음. 알림 담당과 커밋 이후 전달 방식 협의 필요.

### 조회 계약 초안

- 목록 상태: `PENDING_REVIEW`(기본), `PUBLISHED`, `REJECTED`. 임시 저장 DRAFT 제외.
- 전시 ID 내림차순, 다음 페이지는 `displayId < cursor`. size 1~100, 기본 20.
- `items`: displayId, title, requesterId, status, requestedAt.
- `nextCursor`: 다음 페이지가 있을 때 마지막 항목의 displayId, 없으면 null.
- `hasNext`: 다음 페이지 존재 여부. 필터·정렬·페이지 계산은 Display 조회 기능 책임.
- 상세: displayId, title, subtitle, content, requesterId, status, requestedAt,
  processedAt, reviewerId, rejectionReason, startDate, endDate, location, imageUrls.
- 미처리 항목의 processedAt·reviewerId·rejectionReason은 null. 시각은 UTC Instant, 전시 날짜는 LocalDate.
- 상세는 기본 정보·이미지 미리보기 초안. 전체 공개 화면 재현에 필요한 작품·콘텐츠·분야 등은
  Display 담당과 기존 상세 조회 계약 재사용 여부를 맞춘 뒤 확장. 임의의 일반 상세 URL로 대체하지 않음.
- 검색어 필터는 검토 항목으로 남기고 이번 초안에서 제외.

### 활성화 조건

`admin.review.enabled` 기본값은 비활성. Controller와 Service가 등록되지 않음.
두 Port의 실제 어댑터와 JWT ROLE_ADMIN 연결이 완료된 뒤 true로 활성화.
활성화 시 Port Bean이 없으면 애플리케이션 시작 실패. 운영용 mock·항상 성공하는 임시 구현체는 없음.
현재 JWT는 역할을 제공하지 않으므로 이 변경만으로 운영자가 API를 사용할 수는 없음.

차단·신고, 사용자 정지·해제, 콘텐츠 관리는 이번 구현 범위에서 제외.
