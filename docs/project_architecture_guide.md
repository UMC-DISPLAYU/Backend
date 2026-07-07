# 🧩 Project Architecture Guide

> 본 문서는 프로젝트의 백엔드 설계 방향과 코드 작성 규칙을 정리한 문서입니다.  
> 이 프로젝트는 **DDD(Domain-Driven Design)** 관점을 기반으로, 도메인 행위와 비즈니스 규칙을 중심에 두고 구현합니다.

---

## 📚 목차

1. [설계 철학](#-1-설계-철학)
2. [프로젝트 계층 구조](#-2-프로젝트-계층-구조)
3. [권장 파일 구조](#-3-권장-파일-구조)
4. [계층별 책임](#-4-계층별-책임)
5. [DDD 기반 구현 규칙](#-5-ddd-기반-구현-규칙)
6. [DIP 적용 규칙](#-6-dip-적용-규칙)
7. [Repository 설계 규칙](#-7-repository-설계-규칙)
8. [Aggregate 설계 규칙](#-8-aggregate-설계-규칙)
9. [Value Object 매핑 규칙](#-9-value-object-매핑-규칙)
10. [Application Service 작성 규칙](#-10-application-service-작성-규칙)
11. [Presentation Layer 작성 규칙](#-11-presentation-layer-작성-규칙)
12. [Infrastructure Layer 작성 규칙](#-12-infrastructure-layer-작성-규칙)
13. [Transaction 규칙](#-13-transaction-규칙)
14. [CQRS-lite 기반 Query Layer 적용 방향](#-14-cqrs-lite-기반-query-layer-적용-방향)
15. [개발 시 체크리스트](#-15-개발-시-체크리스트)

---

# 🎯 1. 설계 철학

이 프로젝트는 단순 CRUD 중심 설계가 아니라, **도메인의 행위와 규칙을 코드로 표현하는 설계**를 지향합니다.

## ❌ 지양하는 방식

```java
club.setName("러닝 동호회");
club.setStatus(ClubStatus.ACTIVE);
club.setMemberCount(10);
```

위와 같은 방식은 객체를 단순한 데이터 컨테이너로 취급합니다.  
비즈니스 규칙이 서비스 계층이나 컨트롤러에 흩어질 가능성이 큽니다.

---

## ✅ 지향하는 방식

```java
club.join(member);
club.closeRecruitment();
club.changeIntro("함께 러닝해요");
club.delete();
```

DDD 관점에서는 필드 값을 직접 바꾸는 것이 아니라, **도메인에서 실제로 발생하는 행위**를 메서드로 표현합니다.

---

## 🔁 핵심 관점 전환

```text
데이터 중심 설계
"어떤 필드를 저장할까?"
"어떤 컬럼을 수정할까?"

        ↓

행위 중심 설계
"이 도메인은 어떤 행동을 하는가?"
"그 행동에는 어떤 규칙이 있는가?"
"그 규칙은 어느 객체가 책임지는가?"
```

> 객체는 데이터를 담는 그릇이 아니라,  
> **비즈니스 규칙을 가진 도메인 모델**입니다.

---

# 🏛️ 2. 프로젝트 계층 구조

이 프로젝트는 다음 계층 구조를 기준으로 구현합니다.

```text
Presentation Layer
        ↓
Application Layer
        ↓
Domain Layer
        ↑
Infrastructure Layer
```

의존 방향은 기본적으로 아래와 같습니다.

```text
presentation
        ↓
application
        ↓
domain

infrastructure
        ↓ implements / depends on
domain interface
```

즉, `domain`은 `infrastructure`를 알지 않습니다.  
DB, 외부 API, 파일 저장소와 같은 기술 구현은 `infrastructure`에 위치합니다.

---

# 📦 3. 권장 파일 구조

도메인 단위로 패키지를 나누고, 각 도메인 내부에 `presentation`, `application`, `domain`, `infrastructure`를 배치합니다.

```text
src/main/java/com/example/project
 └── club
     ├── presentation
     │   ├── ClubController.java
     │   ├── mapper
     │   │   └── ClubPresentationMapper.java
     │   ├── request
     │   │   └── CreateClubRequest.java
     │   └── response
     │       └── CreateClubResponse.java
     │
     ├── application
     │   ├── CreateClubService.java
     │   ├── ChangeClubIntroService.java
     │   ├── query
     │   │   ├── ClubQueryService.java
     │   │   ├── ClubQueryRepository.java
     │   │   └── ClubSummaryQueryResult.java
     │   ├── command
     │   │   ├── CreateClubCommand.java
     │   │   └── ChangeClubIntroCommand.java
     │   └── result
     │       ├── CreateClubResult.java
     │       └── ChangeClubIntroResult.java
     │
     ├── domain
     │   ├── model
     │   │   ├── Club.java
     │   │   ├── ClubId.java
     │   │   ├── ClubKeyword.java
     │   │   └── ClubImage.java
     │   ├── event
     │   │   └── ClubCreatedEvent.java
     │   ├── repository
     │   │   └── ClubRepository.java
     │   ├── service
     │   │   └── ClubJoinPolicy.java
     │   └── exception
     │       └── ClubDomainException.java
     │
     └── infrastructure
         ├── persistence
         │   ├── ClubJpaEntity.java
         │   ├── SpringDataClubJpaRepository.java
         │   ├── JpaClubRepositoryAdapter.java
         │   ├── ClubPersistenceMapper.java
         │   └── ClubQueryRepositoryAdapter.java
         └── external
             └── ClubExternalClient.java
```

---

## 📌 패키지 구성 원칙

```text
도메인별 패키지
└── presentation
└── application
└── domain
└── infrastructure
```

도메인 단위로 묶는 이유는 기능이 커졌을 때 각 도메인의 응집도를 높이고, 다른 도메인과의 결합을 줄이기 위해서입니다.

---

# 🧭 4. 계층별 책임

| 계층 | 책임 | 포함 요소 |
|---|---|---|
| `presentation` | HTTP 요청/응답 처리 | Controller, Request DTO, Response DTO |
| `application` | 유스케이스 흐름 조율 | Application Service, Command, Result |
| `domain` | 핵심 비즈니스 규칙 | Entity, Value Object, Aggregate, Repository Interface, Domain Service |
| `infrastructure` | 기술 구현 | JPA, DB, 외부 API, 파일 저장소, 메시지 큐 |

---

## Mapper 책임

계층 간 데이터 변환은 Mapper에서 명시적으로 처리합니다.

```text
Presentation Mapper
  → API DTO와 Application DTO 사이의 변환 담당
  → Request DTO -> Command
  → Result -> Response DTO

Persistence Mapper
  → Domain Model과 DB Entity 사이의 변환 담당
  → Domain -> JpaEntity
  → JpaEntity -> Domain
```

Mapper를 두는 이유는 다음과 같습니다.

```text
Controller에 변환 로직이 흩어지는 것을 방지한다.
Application Service가 API DTO를 직접 알지 않도록 한다.
Domain Model이 JPA Entity에 오염되지 않도록 한다.
변환 규칙을 한 곳에서 관리한다.
```

Mapper 구현에는 **MapStruct** 도입을 기준으로 합니다.  
MapStruct는 컴파일 시점에 매핑 코드를 생성하므로, 런타임 리플렉션 기반 매퍼보다 안정적이고 누락된 필드나 타입 불일치를 더 빨리 발견할 수 있습니다.

```java
@Mapper(componentModel = "spring")
public interface ClubPresentationMapper {

    CreateClubCommand toCommand(CreateClubRequest request);

    CreateClubResponse toResponse(CreateClubResult result);
}
```

```java
@Mapper(componentModel = "spring")
public interface ClubPersistenceMapper {

    Club toDomain(ClubJpaEntity entity);

    ClubJpaEntity toEntity(Club club);
}
```

Mapper 작성 규칙은 다음과 같습니다.

```text
Presentation Mapper는 presentation 영역에 둔다.
Persistence Mapper는 infrastructure.persistence 영역에 둔다.
Application Service는 Request DTO와 Response DTO를 직접 다루지 않는다.
Domain Model은 JpaEntity를 직접 알지 않는다.
복잡한 변환 규칙은 Mapper 내부에 명시적으로 작성한다.
```

---

## 전체 흐름

```text
Client
  ↓
Controller
  ↓
Presentation Mapper
  ↓
Application Service
  ↓
Aggregate Root
  ↓
Entity / Value Object
```

저장소 접근 흐름은 다음과 같습니다.

```text
Application Service
  ↓
Domain Repository Interface
  ↑ implements
Infrastructure Repository Adapter
  ↓
Persistence Mapper
  ↓
Spring Data JPA Repository
  ↓
Database
```

---

# 🧱 5. DDD 기반 구현 규칙

## 5.1 Entity

Entity는 식별자를 가지며, 단순 데이터 객체가 아니라 도메인 행위를 가집니다.

```java
public class Club {

    private final ClubId id;
    private String introText;
    private boolean deleted;

    public void changeIntroText(String introText) {
        if (introText == null || introText.isBlank()) {
            throw new IllegalArgumentException("소개글은 비어 있을 수 없습니다.");
        }

        this.introText = introText;
    }

    public void delete() {
        if (this.deleted) {
            throw new IllegalStateException("이미 삭제된 클럽입니다.");
        }

        this.deleted = true;
    }
}
```

### ✅ Entity 작성 규칙

```text
식별자를 가진다.
도메인 행위를 메서드로 표현한다.
무분별한 setter를 만들지 않는다.
상태 변경 시 필요한 검증을 내부에서 수행한다.
```

---

## 5.2 Value Object

Value Object는 식별자가 없고, 값 자체로 동일성을 판단합니다.

```java
public class ClubId {

    private final Long value;

    public ClubId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("클럽 ID는 양수여야 합니다.");
        }

        this.value = value;
    }

    public Long value() {
        return value;
    }
}
```

### ✅ Value Object 작성 규칙

```text
가능하면 불변으로 만든다.
값 검증 로직을 내부에 둔다.
equals(), hashCode()를 구현한다.
원시 타입 남용을 줄인다.
```

---

## 5.3 Aggregate

Aggregate는 관련된 Entity와 Value Object를 하나의 일관성 단위로 묶은 것입니다.

```text
Club Aggregate
 ├── Club              ← Aggregate Root
 ├── ClubImage         ← 내부 Entity
 └── ClubKeyword       ← Value Object
```

Aggregate 외부에서는 내부 객체를 직접 수정하지 않습니다.

```java
// 좋은 예시
club.changeThumbnail(imageId);

// 나쁜 예시
clubImage.markThumbnail();
```

---

# 🔄 6. DIP 적용 규칙

DIP는 고수준 모듈이 저수준 구현체에 직접 의존하지 않도록 만드는 원칙입니다.

## 핵심 구조

```text
Application Service
        ↓ depends on
Domain Interface
        ↑ implements
Infrastructure Implementation
```

---

## 예시

```text
Application
└── CalculateDiscountUseCase
        ↓

Domain
└── RuleDiscounter interface
        ↑ implements

Infrastructure
└── DroolsRuleDiscounter
```

`CalculateDiscountUseCase`는 `DroolsRuleDiscounter`를 직접 알지 않습니다.  
응용 서비스는 `RuleDiscounter` 인터페이스에만 의존합니다.  
실제 구현체는 인프라스트럭처 영역에서 인터페이스를 구현합니다.

---

## DIP 적용 대상

```text
DB Repository
외부 API Client
파일 저장소
메시지 큐
결제 시스템
인증 시스템
Rule Engine
```

---

## DIP 적용 규칙

```text
도메인 또는 응용 영역에 인터페이스를 둔다.
인프라스트럭처 영역에서 해당 인터페이스를 구현한다.
응용 서비스는 구현체가 아니라 인터페이스에 의존한다.
기술 이름이 아니라 도메인 역할을 기준으로 인터페이스 이름을 정한다.
```

예를 들어 `DroolsRuleDiscounter`보다 `RuleDiscounter`가 더 좋은 인터페이스 이름입니다.

---

# 🗃️ 7. Repository 설계 규칙

DDD에서 Repository는 DB 접근 구현체가 아니라, **애그리거트를 저장하고 조회하는 도메인 관점의 인터페이스**입니다.

## Domain Repository Interface

```java
package com.example.club.domain.repository;

import com.example.club.domain.model.Club;
import com.example.club.domain.model.ClubId;

import java.util.Optional;

public interface ClubRepository {

    Optional<Club> findById(ClubId clubId);

    boolean existsByName(String name);

    void save(Club club);

    void delete(Club club);
}
```

---

## Infrastructure Repository Adapter

```java
@Repository
public class JpaClubRepositoryAdapter implements ClubRepository {

    private final SpringDataClubJpaRepository jpaRepository;
    private final ClubPersistenceMapper mapper;

    public JpaClubRepositoryAdapter(
            SpringDataClubJpaRepository jpaRepository,
            ClubPersistenceMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Club> findById(ClubId clubId) {
        return jpaRepository.findById(clubId.value())
                .map(mapper::toDomain);
    }

    @Override
    public void save(Club club) {
        jpaRepository.save(mapper.toEntity(club));
    }
}
```

---

## Repository 의존 방향

```text
Application Service
        ↓
ClubRepository interface
        ↑ implements
JpaClubRepositoryAdapter
        ↓
ClubPersistenceMapper
        ↓
SpringDataClubJpaRepository
        ↓
Database
```

---

## Repository 작성 규칙

```text
Repository 인터페이스는 domain 영역에 둔다.
Repository 구현체는 infrastructure 영역에 둔다.
Repository는 Aggregate Root 단위로 만든다.
내부 Entity별 Repository는 되도록 만들지 않는다.
Repository 인터페이스에는 JPA, SQL, EntityManager 같은 기술 세부사항을 노출하지 않는다.
```

---

# 👑 8. Aggregate 설계 규칙

## 8.1 Aggregate Root

Aggregate Root는 Aggregate 내부 상태 변경의 유일한 진입점입니다.

```text
Application Service
        ↓
Aggregate Root
        ↓
Internal Entity / Value Object
```

---

## 8.2 내부 객체 직접 수정 금지

```java
// 나쁜 예시
ClubImage image = club.getImages().get(0);
image.markThumbnail();
```

```java
// 좋은 예시
club.changeThumbnail(imageId);
```

내부 객체를 직접 수정하면 Aggregate Root가 일관성을 보호할 수 없습니다.

---

## 8.3 Aggregate Root가 보호해야 하는 것

```text
상태 변경 가능 여부
중복 여부
정원 초과 여부
권한 또는 소유자 검증
내부 객체 간의 일관성
```

---

## 8.4 조회는 예외적으로 분리 가능

수정 명령은 Aggregate Root를 통해 처리합니다.  
하지만 단순 조회는 성능을 위해 Query Repository, DTO Projection, Read Model을 사용할 수 있습니다.

```text
Command
  → Aggregate Root 중심

Query
  → DTO / Read Model 중심
```

---

## 8.5 Domain Event 발행 규칙

Aggregate 내부에서 의미 있는 도메인 사건이 발생하면 Domain Event로 표현합니다.  
이 프로젝트에서는 Spring Data의 `AbstractAggregateRoot` 기반 이벤트 발행 방식을 채택합니다.

```text
Aggregate 내부
    ↓
registerEvent()
    ↓
Spring Data JPA가 이벤트 수집
    ↓
Transaction commit 이후 이벤트 발행
    ↓
@EventListener 실행
```

커밋 이후 실행이 반드시 보장되어야 하는 후속 처리는 `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` 사용을 우선합니다.  
트랜잭션 커밋 결과와 무관한 단순 내부 반응은 `@EventListener`로 처리할 수 있습니다.

예시는 다음과 같습니다.

```java
public class Club extends AbstractAggregateRoot<Club> {

    public static Club create(ClubId id, String name, String intro, MemberId hostMemberId) {
        Club club = new Club(id, name, intro, hostMemberId);
        club.registerEvent(new ClubCreatedEvent(id, hostMemberId));
        return club;
    }
}
```

이벤트는 다음 기준으로 사용합니다.

```text
Aggregate 내부 상태 변경이 완료된 뒤 발생한 사실을 표현한다.
이벤트 이름은 과거형으로 작성한다.
이벤트는 명령이 아니라 이미 발생한 결과를 나타낸다.
이벤트 핸들러에서 원래 트랜잭션의 핵심 상태 변경을 대신 처리하지 않는다.
알림, 로그, 통계, 후속 비동기 처리처럼 부가적인 반응을 분리할 때 사용한다.
```

주의할 점은 다음과 같습니다.

```text
Domain Event는 도메인 의미를 담고, 기술 구현 세부사항을 담지 않는다.
Aggregate는 이벤트 핸들러를 직접 호출하지 않는다.
이벤트 발행은 Repository save 이후 트랜잭션 커밋 흐름과 함께 처리한다.
외부 시스템 연동이 필요한 경우 실패 보상, 재시도, Outbox 패턴 도입 여부를 별도로 검토한다.
```

---

# 💎 9. Value Object 매핑 규칙

도메인에서 Value Object로 표현하더라도, DB 저장 방식은 상황에 따라 달라질 수 있습니다.

---

## 9.1 엔티티 테이블 컬럼에 매핑되는 Value Object

하나의 테이블에 Entity와 Value Object가 함께 저장되는 경우입니다.

```java
@Embeddable
public class Money {

    private Integer amount;
    private String currency;

    protected Money() {
    }
}
```

```java
@Entity
public class Product {

    @Id
    private Long id;

    @Embedded
    private Money price;
}
```

DB 구조는 다음과 같습니다.

```text
products
 ├── id
 ├── name
 ├── price_amount
 └── price_currency
```

---

## 9.2 별도 테이블에 저장되는 Value Object

Value Object 컬렉션은 `@ElementCollection`으로 별도 테이블에 저장할 수 있습니다.

```java
@ElementCollection
@CollectionTable(
        name = "club_keywords",
        joinColumns = @JoinColumn(name = "club_id")
)
private Set<ClubKeyword> keywords = new HashSet<>();
```

DB 구조는 다음과 같습니다.

```text
clubs
 ├── id
 └── name

club_keywords
 ├── club_id
 └── keyword
```

---

## 9.3 식별자 자체를 Value Object로 만드는 경우

```java
@Embeddable
public class ClubId {

    private Long value;

    protected ClubId() {
    }

    public ClubId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("클럽 ID는 양수여야 합니다.");
        }

        this.value = value;
    }
}
```

장점은 다음과 같습니다.

```text
원시 타입 남용 방지
ID 검증 로직 추가 가능
MemberId와 ClubId 혼동 방지
ID 자체에 작은 기능 추가 가능
```

---

## 9.4 개념적으로는 Value이지만 Entity로 매핑하는 경우

도메인적으로는 값처럼 보이지만, 다음 요구사항이 있으면 Entity로 매핑할 수 있습니다.

```text
개별 식별자가 필요하다.
부분 수정이 자주 발생한다.
개별 삭제가 필요하다.
순서 변경이 필요하다.
운영, 감사, 로그, 추적 요구가 있다.
```

예시:

```text
OrderLine
ClubImage
ExhibitionImage
ReviewImage
```

중요한 점은 다음과 같습니다.

```text
JPA Entity라고 해서 항상 Aggregate Root는 아니다.

Club = Aggregate Root
ClubImage = Aggregate 내부 Entity
```

---

## 9.5 AttributeConverter

두 개 이상의 프로퍼티를 가진 Value Object를 DB 한 컬럼에 저장하고 싶다면 `AttributeConverter`를 사용할 수 있습니다.

```java
public class Money {
    private final int amount;
    private final String currency;
}
```

DB에는 다음처럼 저장할 수 있습니다.

```text
10000:KRW
```

```java
@Converter
public class MoneyConverter implements AttributeConverter<Money, String> {

    @Override
    public String convertToDatabaseColumn(Money attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.amount() + ":" + attribute.currency();
    }

    @Override
    public Money convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        String[] parts = dbData.split(":");
        return Money.of(Integer.parseInt(parts[0]), parts[1]);
    }
}
```

---

# 🧭 10. Application Service 작성 규칙

Application Service는 표현 영역과 도메인 영역을 연결하는 유스케이스 조율자입니다.

> Application Service는 도메인 로직을 직접 구현하지 않습니다.  
> Repository에서 Aggregate를 조회하고, Aggregate의 도메인 기능을 실행한 뒤, 결과를 반환합니다.

---

## 10.1 기존 Aggregate 수정

```java
public class ChangeClubIntroService {

    private final ClubRepository clubRepository;

    public ChangeClubIntroService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public ChangeClubIntroResult changeIntro(ChangeClubIntroCommand command) {
        // 1. Repository에서 Aggregate 조회
        Club club = clubRepository.findById(new ClubId(command.clubId()))
                .orElseThrow(() -> new IllegalArgumentException("클럽을 찾을 수 없습니다."));

        // 2. Aggregate의 도메인 기능 실행
        club.changeIntro(command.newIntro());

        // 3. Aggregate 저장
        clubRepository.save(club);

        // 4. Result 반환
        return new ChangeClubIntroResult(
                club.id().value(),
                club.intro()
        );
    }
}
```

---

## 10.2 Aggregate 생성

```java
public class CreateClubService {

    private final ClubRepository clubRepository;
    private final ClubIdGenerator clubIdGenerator;

    public CreateClubService(
            ClubRepository clubRepository,
            ClubIdGenerator clubIdGenerator
    ) {
        this.clubRepository = clubRepository;
        this.clubIdGenerator = clubIdGenerator;
    }

    public CreateClubResult createClub(CreateClubCommand command) {
        // 1. 중복 등 데이터 유효성 검사
        if (clubRepository.existsByName(command.name())) {
            throw new IllegalArgumentException("이미 존재하는 클럽 이름입니다.");
        }

        // 2. Aggregate 식별자 생성
        ClubId clubId = clubIdGenerator.generate();

        // 3. Aggregate 생성
        Club club = Club.create(
                clubId,
                command.name(),
                command.intro(),
                command.hostMemberId()
        );

        // 4. Repository에 Aggregate 저장
        clubRepository.save(club);

        // 5. Result 반환
        return new CreateClubResult(
                club.id().value(),
                club.name()
        );
    }
}
```

---

## Application Service 책임

```text
Command 입력 처리
Repository 호출
Aggregate 생성 또는 조회
Aggregate 도메인 메서드 호출
Transaction 경계 설정
Result 반환
권한 검사 조율
```

---

## Application Service 금지 사항

```text
도메인 로직 직접 구현 금지
Controller 객체 의존 금지
HttpServletRequest, HttpSession 전달 금지
JPA EntityManager 직접 사용 지양
외부 API 구현체 직접 의존 지양
```

---

# 🎮 11. Presentation Layer 작성 규칙

Presentation Layer는 HTTP 요청과 응답을 담당합니다.

## 책임

```text
HTTP 요청 수신
Request DTO 검증
Command 변환
Application Service 호출
Result를 Response DTO로 변환
HTTP 응답 반환
```

---

## 예시

```java
@RestController
@RequestMapping("/api/v1/clubs")
public class ClubController {

    private final CreateClubService createClubService;
    private final ClubPresentationMapper mapper;

    @PostMapping
    public CreateClubResponse create(@RequestBody CreateClubRequest request) {
        CreateClubCommand command = mapper.toCommand(request);
        CreateClubResult result = createClubService.createClub(command);
        return mapper.toResponse(result);
    }
}
```

---

## Presentation Layer 금지 사항

```text
도메인 로직 구현 금지
Repository 직접 호출 금지
JPA Entity 직접 반환 금지
복잡한 비즈니스 검증 금지
```

---

# 🏗️ 12. Infrastructure Layer 작성 규칙

Infrastructure Layer는 기술 구현을 담당합니다.

## 포함 대상

```text
JPA Repository 구현체
Spring Data JPA Repository
JpaEntity
Persistence Mapper
Query Repository 구현체
외부 API Client
파일 저장소 구현체
메시지 큐 Producer / Consumer
설정 클래스
```

---

## 규칙

```text
Domain Repository Interface를 구현한다.
DB 접근 기술을 캡슐화한다.
JpaEntity와 Domain Model 변환은 Persistence Mapper에서 처리한다.
Application Service가 인프라 구현체를 직접 알지 않도록 한다.
```

---

# 🔐 13. Transaction 규칙

트랜잭션 경계는 기본적으로 Application Service에 둡니다.

```java
@Transactional
public ChangeClubIntroResult changeIntro(ChangeClubIntroCommand command) {
    ...
}
```

## 이유

```text
하나의 유스케이스 단위로 트랜잭션을 관리하기 좋다.
여러 Repository 작업을 하나의 트랜잭션으로 묶을 수 있다.
도메인 객체는 트랜잭션 기술을 몰라도 된다.
```

---

# 🔎 14. CQRS-lite 기반 Query Layer 적용 방향

조회와 명령은 목적이 다르므로 필요하면 분리합니다.
이 프로젝트에서는 완전한 CQRS보다 단순하고 운영 부담이 낮은 **CQRS-lite 기반 Query Layer**를 우선 적용합니다.

```text
Command
 ├── 상태 변경
 ├── Aggregate Root 중심
 └── 도메인 규칙 실행

Query
 ├── 데이터 조회
 ├── DTO / Projection 중심
 └── 성능 최적화 가능
```

CQRS-lite는 다음 정도의 분리를 의미합니다.

```text
Command는 Application Service와 Aggregate Root 중심으로 처리한다.
Query는 별도의 Query Service와 Query Repository를 통해 처리한다.
쓰기 모델과 읽기 모델의 DB를 물리적으로 분리하지는 않는다.
조회 성능이 필요한 경우 DTO Projection, fetch join, QueryDSL 등을 사용할 수 있다.
조회 전용 결과 객체는 Domain Model 대신 Query Result 또는 Read Model로 반환한다.
```

## 적용 예시

```text
클럽 생성
클럽 수정
클럽 삭제
    → Command Service

클럽 목록 조회
클럽 상세 조회
검색 조건 기반 조회
    → Query Repository / Read Model
```

## Query Layer 권장 구조

```text
presentation
  ↓
ClubQueryService
  ↓
ClubQueryRepository
  ↓
Infrastructure Query Repository Adapter
  ↓
Database
```

예시는 다음과 같습니다.

```java
public interface ClubQueryRepository {

    List<ClubSummaryQueryResult> findClubSummaries(ClubSearchCondition condition);
}
```

```java
@Service
@Transactional(readOnly = true)
public class ClubQueryService {

    private final ClubQueryRepository clubQueryRepository;

    public List<ClubSummaryQueryResult> getClubSummaries(ClubSearchCondition condition) {
        return clubQueryRepository.findClubSummaries(condition);
    }
}
```

Query Layer 작성 규칙은 다음과 같습니다.

```text
조회 전용 로직은 Command Application Service에 섞지 않는다.
Query Service는 상태 변경을 수행하지 않는다.
Query Repository는 조회 최적화를 위해 DTO Projection을 사용할 수 있다.
단순 조회를 위해 Aggregate 전체를 복원하지 않아도 된다.
Query Result는 응답에 필요한 데이터 중심으로 설계한다.
```

---

# ✅ 15. 개발 시 체크리스트

## Domain

```text
엔티티가 단순 setter 중심으로 작성되지 않았는가?
도메인 행위가 메서드 이름에 드러나는가?
Value Object가 원시 타입을 의미 있게 감싸고 있는가?
Aggregate Root가 내부 상태 변경을 통제하는가?
Domain이 Infrastructure를 직접 의존하지 않는가?
```

---

## Application

```text
Application Service가 유스케이스 흐름만 조율하는가?
도메인 로직이 Application Service에 과도하게 들어가 있지 않은가?
Transaction 경계가 적절히 설정되어 있는가?
Controller 객체나 HTTP 객체에 의존하지 않는가?
Command Service와 Query Service가 분리되어 있는가?
```

---

## Infrastructure

```text
Repository 구현체가 Domain Repository Interface를 구현하는가?
JpaEntity와 Domain Model 변환이 Persistence Mapper에 격리되어 있는가?
외부 API, DB, 파일 저장소 구현 세부사항이 Infrastructure에 모여 있는가?
```

---

## Presentation

```text
Controller가 Request 검증과 응답 변환에 집중하는가?
Controller에서 Repository를 직접 호출하지 않는가?
Controller에서 도메인 로직을 수행하지 않는가?
API DTO와 Application DTO 변환을 Presentation Mapper로 위임하는가?
```

---

# ✨ 최종 정리

이 프로젝트는 다음 원칙을 기준으로 구현합니다.

```text
1. 도메인을 데이터가 아니라 행위와 규칙 중심으로 설계한다.
2. Application Service는 유스케이스 흐름만 조율한다.
3. Domain Model은 비즈니스 규칙을 직접 표현한다.
4. Repository Interface는 Domain 영역에 둔다.
5. Repository 구현체는 Infrastructure 영역에 둔다.
6. Aggregate Root를 통해 내부 상태 변경을 통제한다.
7. DB, 외부 API, 파일 저장소 같은 기술 구현은 Infrastructure에 격리한다.
8. Presentation Mapper와 Persistence Mapper로 계층 간 변환 책임을 분리한다.
9. Command와 Query는 CQRS-lite 기준으로 분리한다.
10. Aggregate 내부 Domain Event는 AbstractAggregateRoot와 registerEvent()를 기준으로 발행한다.
```

> **우리 프로젝트의 설계 기준은 "필드 변경"이 아니라 "도메인 행위"입니다.**  
> 따라서 코드는 `setStatus()`보다 `cancel()`, `join()`, `disable()`, `changeIntro()`처럼  
> 비즈니스 행위를 드러내는 방향으로 작성합니다.
