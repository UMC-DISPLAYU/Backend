# Code Convention

이 문서는 현재 Spring Boot 코드베이스에서 실제로 사용 중인 구조와 앞으로 맞춰야 할 구현 규칙을 정리한다. 일반 Java/Spring 관례보다 이 프로젝트의 기존 패턴을 우선한다.

## 1. Package Structure

- 도메인 루트는 `com.example.demo.domain.{domainName}` 형태를 사용한다. 예: `display`, `archive`, `lounge`, `user`, `personalartwork`.
- 도메인 내부는 기본적으로 `presentation`, `application`, `domain`, `infrastructure` 계층으로 나눈다.
- `presentation`에는 `Controller`, `docs`, `mapper`, `request`, `response`를 둔다.
- `application`은 `command`, `query`, `result`, 필요 시 `service`, `usecase`, `mapper`로 나뉜다.
- `domain`은 `aggregate`, `entity`, `vo`, `type/enums`, `repository`, `error`를 둔다.
- `infrastructure`는 `persistence`, `oauth`, `mail`처럼 기술 구현 기준으로 나누고, JPA 구현은 `persistence`와 `persistence/adapter`에 둔다.

## 2. Naming Convention

- 클래스명은 역할이 드러나게 끝낸다: `DisplayController`, `UpdateDisplayService`, `DisplayPresentationMapper`, `JpaDisplayRepositoryAdapter`.
- Command/Query/Result record는 유스케이스명 또는 응답 의미를 붙인다: `CreateDisplayCommand`, `SearchDisplayQuery`, `DisplayDetailResult`.
- Repository port는 domain에 `{Aggregate}Repository`, query port는 `{Feature}QueryRepository`를 사용한다.
- Spring Data repository는 `SpringData{Name}JpaRepository`, 구현 adapter는 `Jpa{Name}RepositoryAdapter` 또는 `Jpa{Name}QueryRepositoryAdapter`를 사용한다.
- 위 adapter 규칙은 `domain/repository`의 repository를 구현하는 경우에만 적용한다. `application/port`의 port를 구현하는 adapter는 port의 역할이 드러나게 명명한다. 예: `DisplayContentPublicationPort -> JpaDisplayContentPublicationAdapter`, `SchoolEmailSenderPort -> SchoolEmailSenderAdapter`, `DisplayListCacheEvictionPort -> DisplayListCacheEvictor`.
- 상수는 `UPPER_SNAKE_CASE`, 임시 인증 값은 `TEMP_USER_ID`처럼 명확히 표시한다.

## 3. Controller & Swagger

- Controller는 HTTP 요청/응답 조립만 담당하고, 비즈니스 규칙은 Application Service로 넘긴다.
- 공통 응답은 항상 `ApiResponseBody.success(..., request)`로 감싼다.
- 현재 Swagger 작성 방식은 두 가지가 혼용된다.
  - Controller가 docs interface를 implements: `ArchiveDisplayController`, `LoungePostController`, `AuthController`.
  - Controller에 직접 `@Operation`, `@ApiResponse` 작성: `DisplayController`, `HealthController`.
- 권장 통일 방식: 신규 API는 `presentation/docs/*ControllerDocs` 인터페이스에 Swagger 문서를 두고 Controller는 `implements` 한다.
- Swagger 예시는 응답 래퍼까지 포함해 실제 `ApiResponseBody` 구조와 맞춘다.

## 4. DTO, Command, Query, Result

- Request/Response DTO는 대부분 Java `record`를 사용한다.
- Request DTO는 HTTP 입력과 validation 중심으로 유지한다. 도메인 변환은 가능하면 mapper에서 수행한다.
- Command는 쓰기 유스케이스 입력, Query는 조회 조건, Result는 application 출력으로 사용한다.
- Result는 domain/entity를 그대로 노출하지 않고 응답에 필요한 값만 record로 구성한다.
- 조회 결과는 `CursorResponse`, `PaginationResult`, `ExhibitionResult`처럼 내부 record를 활용한다.

## 5. Mapper

- Presentation Mapper는 `@Component` 클래스로 작성하고 `Request -> Command`, `Result -> Response` 변환을 담당한다.
- enum 이름이 API와 도메인에서 다르면 mapper에서 switch로 명시 변환한다. 예: `MEDIA -> VIDEO`, `TASK -> ASSIGNMENTS`.
- Controller에서 직접 변환 로직을 길게 작성하지 않는다.
- 현재 MapStruct 의존성은 있으나 실제 코드는 대부분 수동 mapper다. 신규 코드는 기존 수동 mapper 스타일을 우선한다.
- Domain과 JPA Entity 간 별도 mapper는 거의 없다. 현재 Aggregate가 JPA Entity 역할도 함께 하므로 adapter는 Spring Data repository에 위임하는 형태가 많다.

## 6. Domain Model

- Aggregate/Entity는 `@Entity`, `@Getter`, protected 기본 생성자, 명시 생성자 또는 static factory를 사용한다.
- 필드 변경은 setter 대신 행위 메서드로 표현한다. 예: `changeContent`, `publish`, `delete`, `restore`, `replaceImages`.
- 내부 컬렉션은 `List`로 관리하고 외부에는 `Collections.unmodifiableList(...)`로 노출한다.
- Value Object는 `@Embeddable`을 사용하고 생성자에서 null/범위/blank 검증을 수행한다.
- soft delete는 `SoftDeleteBaseEntity` 또는 `deletedAt` 필드와 `delete()/restore()/isDeleted()` 계열 메서드로 처리한다.

## 7. Service & Transaction

- Application Service는 유스케이스 흐름 조율, repository 호출, transaction 경계, Result 생성만 담당한다.
- 쓰기 서비스에는 `@Transactional`, 조회 서비스에는 `@Transactional(readOnly = true)`를 메서드 단위로 붙이는 패턴이 많다.
- command 패키지에는 생성/수정/삭제/토글 유스케이스를 둔다.
- query 또는 service 패키지에는 조회 유스케이스를 둔다. `display`는 `application/service + usecase`가 있고, 다른 도메인은 `application/query` 서비스가 많다.
- 시간 기준 로직은 `Clock` 주입을 우선한다. 예: display 조회 서비스.

## 8. Repository & Persistence

- domain repository interface는 `domain/repository`에 둔다.
- infrastructure adapter는 domain repository를 구현하고 Spring Data repository에 위임한다.
- Spring Data repository는 infrastructure에만 둔다. domain/application에서 직접 의존하지 않는다.
- 복잡한 조회는 query repository port와 adapter를 분리한다. 예: `SearchDisplayQueryRepository`, `JpaSearchDisplayQueryRepositoryAdapter`.
- 단순 존재 확인용 repository도 domain port로 분리하는 패턴이 있다. 예: `UserExistenceRepository`, `DisplayArtworkExistenceRepository`.

## 9. Exception & API Response

- 비즈니스 예외는 `BusinessException(BaseErrorCode)`로 던진다.
- 공통 에러는 `GlobalErrorCode`, 도메인별 에러는 `{Domain}ErrorCode` enum으로 둔다.
- ErrorCode enum은 `BaseErrorCode`를 구현하고 `HttpStatus`, `code`, `message`를 가진다.
- `GlobalExceptionHandler`가 validation, type mismatch, malformed JSON, method/content-type 오류, fallback exception을 공통 처리한다.
- 성공/실패 응답 구조는 `resultType`, `success`, `error`, `meta`를 유지한다.

## 10. Validation

- Request body 검증은 request record 필드에 `@NotBlank`, `@NotNull`, `@Size`, `@Positive`, `@Min`, `@Max` 등을 붙인다.
- 조건부 검증은 request record 내부 `@AssertTrue` 메서드로 처리한다.
- Query/path parameter 검증은 docs interface 또는 Controller 파라미터에 붙이고 Controller에는 `@Validated`를 사용한다.
- 도메인 불변식은 DTO validation에만 맡기지 말고 Aggregate/VO 생성자 또는 행위 메서드에서도 방어한다.
- 인증 구현 전 임시 `userId` 입력은 명시적으로 TODO 또는 request 필드로 유지하되, 인증 도입 시 제거 대상으로 기록한다.

## 11. Lombok & Java Style

- Entity/Aggregate에는 주로 `@Getter`만 사용하고 setter는 노출하지 않는다.
- 생성자 주입은 명시 생성자와 `@RequiredArgsConstructor`가 혼용된다. 신규 코드는 한 클래스 안에서 한 방식만 사용한다.
- DTO/Command/Query/Result는 record를 우선한다.
- JPA Entity 기본 생성자는 `protected`로 둔다.
- 포맷팅은 Spotless `googleJavaFormat()`, `importOrder()`, `removeUnusedImports()`, `formatAnnotations()`를 따른다. Checkstyle은 현재 없다.

## 12. Test Convention

- Controller 통합 테스트는 `@SpringBootTest`, `@AutoConfigureMockMvc`, `@ActiveProfiles("test")`, 필요 시 `@Transactional`을 사용한다.
- Repository adapter 테스트는 `@DataJpaTest`, `@Import({Adapter.class, JpaAuditingConfig.class})`를 사용한다.
- 테스트 메서드명은 설명형 camelCase를 사용한다. 예: `updateDisplayReturnsForbiddenWhenRequesterIsNotTeamLeader`.
- MockMvc 검증은 `status()`와 `jsonPath()`로 공통 응답 구조까지 확인한다.
- 테스트 fixture는 private static helper로 만든다. 예: `displayWithTeamMembers()`, `publishedDisplay(...)`.

## 13. API Convention

- API prefix는 `/api/v1`을 사용한다.
- 리소스 기반 URL을 우선한다. 예: `/api/v1/lounge/posts/{loungePostId}`, `/api/v1/archives/exhibitions`.
- 생성은 `POST`, 수정은 `PATCH`, 삭제/취소는 `DELETE`를 주로 사용한다.
- 일부 legacy/신규 기능에서 토글 취소를 `PATCH`로 쓰는 API가 있다. 예: `/api/v1/display/like`.
- 커서 조회는 `cursorId`, `cursor`, `size` query parameter를 사용하고 기본 size는 대체로 `10`이다.

## 14. 현재 혼용 중인 패턴과 통일 권장 사항

### Swagger 문서화

- 현재 혼용 방식: Controller 직접 Swagger 어노테이션 방식과 `presentation/docs` interface 방식이 함께 존재한다.
- 권장 통일 방식: 신규 API는 docs interface에 Swagger를 두고 Controller는 HTTP 매핑과 서비스 호출에 집중한다.
- 이유: Controller가 짧아지고, 문서 예시/응답 설명을 별도 파일에서 관리할 수 있다.

### Request DTO의 변환 책임

- 현재 혼용 방식: 일부 request record가 `toCommand()`를 직접 가지고, 일부는 Presentation Mapper가 변환한다.
- 권장 통일 방식: 신규/수정 API는 Presentation Mapper에서 `Request -> Command` 변환을 담당한다.
- 이유: request DTO가 application/domain 타입에 직접 의존하는 것을 줄이고 변환 규칙을 한 곳에 모을 수 있다.

### Application 조회 패키지

- 현재 혼용 방식: `display`는 `application/service`와 `usecase`를 사용하고, 다른 도메인은 `application/query` service가 많다.
- 권장 통일 방식: 새 조회 기능은 도메인 기존 패턴을 따른다. display 안에서는 usecase interface가 이미 있으면 구현체를 `application/service`에 둔다.
- 이유: 전체 대규모 이동 없이 도메인 내부 일관성을 유지한다.

### Persistence Mapper

- 현재 혼용 방식: 아키텍처 문서는 persistence mapper를 권장하지만, 실제 코드는 Aggregate가 JPA Entity를 겸하는 경우가 많다.
- 권장 통일 방식: 현 구조에서는 불필요한 JpaEntity/Mapper를 새로 만들지 않는다. 외부 테이블 조회 전용 projection/entity가 필요할 때만 infrastructure에 둔다.
- 이유: 현재 코드의 복잡도를 유지하면서 domain이 infrastructure 구현체를 직접 의존하지 않는 선을 지킨다.

### ErrorCode 위치

- 현재 혼용 방식: `domain/*/domain/error`, `domain/user/exception`, `global/error`가 함께 사용된다.
- 권장 통일 방식: 신규 도메인 에러는 가능하면 `{domain}/domain/error/{Domain}ErrorCode`에 둔다.
- 이유: 도메인별 예외 코드를 찾기 쉽고 `BusinessException` 처리 구조와 맞다.

### 빌드 파일 포맷

- 현재 혼용 방식: `build.gradle`의 indentation/blank line이 Spotless 적용 전 스타일과 일부 섞여 있다.
- 권장 통일 방식: 변경 전후 `./gradlew spotlessApply` 또는 최소 `./gradlew spotlessCheck`를 실행한다.
- 이유: CI에서 `spotlessJavaCheck`/`spotlessMiscCheck`가 실패하는 것을 방지한다.

## 15. 컨벤션 불일치 체크리스트

새 기능을 구현하거나 기존 코드를 수정할 때 아래 항목은 특히 확인한다. 이 항목들은 현재 코드에서 혼용되거나 컨벤션과 어긋난 사례가 확인된 부분이다.

### Controller 생성자 주입

- 현재 불일치: 일부 Controller/Service는 명시 생성자, 일부는 `@RequiredArgsConstructor`를 사용한다.
- 권장: 한 클래스 안에서는 하나의 방식만 사용한다. 신규 Controller는 도메인 주변 코드가 쓰는 방식을 따른다.
- 수정 필요성: 생성자 주입 방식 자체보다 파일별 일관성이 중요하다.

### Controller Validation 위치

- 현재 불일치: path/query parameter validation이 docs interface에 있는 경우와 Controller 파라미터에 있는 경우가 섞여 있다.
- 권장: docs interface를 implements하는 Controller는 docs interface에 Swagger와 parameter validation을 함께 둔다.
- 수정 필요성: `@Validated`가 Controller에 없으면 parameter validation이 동작하지 않을 수 있으므로 신규 Controller에는 명시한다.

### 인증 전 임시 사용자 처리

- 현재 불일치: `TEMP_USER_ID` 상수 사용, request body의 `userId` 입력, path/query 기반 임시 사용자 처리가 섞여 있다.
- 권장: 인증 전에는 기존 API 흐름을 따르되 `TEMP_USER_ID` 또는 request `userId` 사용 이유를 주석/TODO로 남긴다.
- 수정 필요성: 인증 도입 시 제거 대상이므로 비즈니스 규칙에 임시 사용자 값을 깊게 섞지 않는다.

### API 명명과 도메인 명명

- 현재 불일치: API에서는 `exhibitionId`, 도메인에서는 `displayId`를 쓰는 등 외부 용어와 내부 용어가 다르다.
- 권장: 외부 API 용어와 내부 도메인 용어 변환은 Presentation Mapper에서만 수행한다.
- 수정 필요성: Controller나 Service에서 용어 변환이 흩어지면 응답 필드명 변경 시 수정 범위가 커진다.

### Like/Archive 토글 API

- 현재 불일치: 좋아요/저장 취소가 `DELETE`인 API와 `PATCH`인 API가 함께 존재한다.
- 권장: 신규 토글 리소스는 생성 `POST`, 취소 `DELETE`를 우선한다. 기존 `PATCH /display/like`는 호환성 때문에 유지한다.
- 수정 필요성: 같은 성격의 토글 API가 서로 다른 HTTP method를 쓰면 프론트엔드 구현과 문서 이해가 어려워진다.

### Query Repository 위치

- 현재 불일치: display query repository는 `application/query`에 있고, 일반 repository는 `domain/repository`에 있다.
- 권장: 현재 display 구조에서는 기존 위치를 유지한다. 새 도메인의 저장소 port는 기본적으로 `domain/repository`에 둔다.
- 수정 필요성: 대규모 이동 없이 도메인 내부 일관성을 우선한다. 단, infrastructure 구현체는 반드시 infrastructure에 둔다.

### Domain Error Package

- 현재 불일치: `domain/user/exception`과 `{domain}/domain/error`가 함께 존재한다.
- 권장: 신규 도메인 에러는 `{domain}/domain/error`에 둔다. user 도메인의 기존 exception 패키지는 별도 정리 전까지 유지한다.
- 수정 필요성: 예외 위치가 분산되면 ErrorCode 검색과 재사용이 어려워진다.

### Service Package 분리

- 현재 불일치: `application/command/*Service`, `application/query/*Service`, `application/service/*Service`가 도메인별로 다르게 쓰인다.
- 권장: 새 코드는 해당 도메인의 기존 패키지 규칙을 먼저 따른다. 도메인 내에서 새로 나눌 때는 쓰기 `command`, 읽기 `query`를 우선한다.
- 수정 필요성: 전체 패키지 재배치보다 도메인 내부 탐색 비용을 줄이는 것이 우선이다.

### Persistence 구현 방식

- 현재 불일치: Aggregate가 JPA Entity를 겸하는 구조와 reference/projection용 JpaEntity가 infrastructure에 따로 있는 구조가 공존한다.
- 권장: 핵심 Aggregate는 현재처럼 JPA 매핑을 직접 가진다. 단순 존재 확인, 외부 도메인 참조, 조회 전용 구조는 infrastructure reference/projection entity를 사용한다.
- 수정 필요성: 불필요한 persistence mapper 추가는 피하되, 다른 도메인 aggregate를 직접 의존하는 persistence 코드는 만들지 않는다.

### Request DTO의 도메인 타입 의존

- 현재 불일치: 일부 request DTO는 enum/command 변환을 직접 갖고, 일부는 mapper에서 처리한다.
- 권장: request DTO는 입력 필드와 validation만 갖고, 도메인 enum/command 변환은 mapper가 담당한다.
- 수정 필요성: presentation 계층이 domain/application 타입에 과도하게 의존하지 않도록 한다.

### Result와 Response 필드 구성

- 현재 불일치: 일부 response는 result 구조를 거의 그대로 복사하고, 일부는 API 용어로 재구성한다.
- 권장: application result는 유스케이스 결과 용어, response는 API 계약 용어를 사용한다. 변환은 mapper에 둔다.
- 수정 필요성: API 필드명 변경이 application service까지 번지는 것을 막는다.

### Environment Property

- 현재 불일치: `application-local.yaml`에는 기본값이 있는 환경변수가 있고, `application-dev.yaml`은 필수 placeholder 위주다.
- 권장: dev/운영 프로파일은 누락 시 빠르게 실패하도록 필수 placeholder를 사용한다. local/test만 안전한 기본값을 허용한다.
- 수정 필요성: 클라우드 배포에서 GitHub Secrets에만 값이 있고 CD `.env`에 누락되는 문제가 반복될 수 있다.
