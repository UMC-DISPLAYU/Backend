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
| GET | `/api/v1/admin/displays` | 심사 목록 | 심사 상태, 페이지 조건은 협의 |
| GET | `/api/v1/admin/displays/{displayId}` | 심사 상세 | 전시 ID |
| POST | `/api/v1/admin/displays/{displayId}/approve` | 승인 | 전시 ID |
| POST | `/api/v1/admin/displays/{displayId}/reject` | 반려 | 전시 ID, `reason` |

- 전체 API는 ADMIN만 접근. 미인증 401, 일반 사용자 403.
- 기존 공통 응답·예외 처리 방식을 사용. 성공 상태 코드와 상세 필드는 계약 확정 시 명시.
- 반려 사유는 필수. 길이 제한과 오류 코드는 Display 담당자와 합의.
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

초기 구성은 이 문서와 `src/main/java/com/example/demo/domain/admin/package-info.java`에 한정.
계약 합의 후 `admin.presentation`의 Controller·DTO·Mapper와
`admin.application`의 심사 처리 코드를 추가하고 `src/test/java/com/example/demo/domain/admin`에서 검증.
연동 어댑터는 계약상 필요할 때만 추가. User·Display 코드는 직접 수정하지 않음.
역할 계약 확정 후 `global.security`의 JWT 인증 흐름과 `global.config.SecurityConfig`는
역할 연결·관리자 API 접근 제한에 필요한 범위에서 수정하고 권한 테스트 추가.

브랜치: `feat/DU-230`

커밋은 `docs: 내용`, `chore: 내용`, `feat: 내용` 형식으로 계약 문서, 초기 구성, 실제 기능별로 분리.
Java 코드 변경 후 `spotlessCheck`와 관련 검증 실행.
