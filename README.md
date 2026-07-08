# DisplayU Backend

> 대학생 졸업전시 플랫폼 **DisplayU**의 백엔드 서버입니다.  
> Spring Boot 기반 REST API 서버로, 전시 정보 관리와 API 제공을 담당합니다.

## 📌 Overview

DisplayU Backend는 졸업전시 정보를 등록하고 조회할 수 있는 플랫폼의 서버 애플리케이션입니다.

주요 목표는 다음과 같습니다.

- 대학생 졸업전시 데이터 관리
- 클라이언트에게 REST API 제공
- Swagger UI 기반 API 문서 제공
- 로컬 개발 환경과 개발 서버 환경 분리
- GitHub Actions 기반 CI를 통한 코드 품질 유지
- 도메인 중심의 패키지 구조 적용

## 🛠 Tech Stack

| Category | Stack |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4.0.7 |
| Build Tool | Gradle |
| Web | Spring Web MVC |
| Validation | Spring Validation |
| Persistence | Spring Data JPA, JDBC |
| Database | MySQL 8.4 |
| Test Database | H2 Database |
| API Docs | Springdoc OpenAPI, Swagger UI |
| Monitoring | Spring Boot Actuator |
| Code Style | Spotless, Google Java Format |
| CI | GitHub Actions |

## 🗂 Project Structure

DisplayU Backend는 도메인 단위로 패키지를 나누고, 각 도메인 내부에서 `presentation`, `application`, `domain`, `infrastructure` 계층을 구분합니다.

```text
src
├── main
│   ├── java
│   │   └── com.displayu.backend
│   │       ├── DisplayUBackendApplication.java
│   │       │
│   │       ├── exhibition
│   │       │   ├── presentation
│   │       │   │   ├── ExhibitionController.java
│   │       │   │   ├── mapper
│   │       │   │   │   └── ExhibitionPresentationMapper.java
│   │       │   │   ├── request
│   │       │   │   │   └── CreateExhibitionRequest.java
│   │       │   │   └── response
│   │       │   │       └── ExhibitionResponse.java
│   │       │   │
│   │       │   ├── application
│   │       │   │   ├── usecase
│   │       │   │   │   └── CreateExhibitionUseCase.java
│   │       │   │   ├── service
│   │       │   │   │   └── ExhibitionService.java
│   │       │   │   ├── query
│   │       │   │   │   ├── ExhibitionQueryService.java
│   │       │   │   │   ├── ExhibitionQueryRepository.java
│   │       │   │   │   └── ExhibitionSummaryQueryResult.java
│   │       │   │   ├── command
│   │       │   │   │   └── CreateExhibitionCommand.java
│   │       │   │   └── result
│   │       │   │       └── ExhibitionResult.java
│   │       │   │
│   │       │   ├── domain
│   │       │   │   ├── entity
│   │       │   │   │   └── Exhibition.java
│   │       │   │   ├── event
│   │       │   │   │   └── ExhibitionCreatedEvent.java
│   │       │   │   ├── repository
│   │       │   │   │   └── ExhibitionRepository.java
│   │       │   │   ├── vo
│   │       │   │   │   └── ExhibitionTitle.java
│   │       │   │   ├── policy
│   │       │   │   │   └── ExhibitionPolicy.java
│   │       │   │   └── error
│   │       │   │       └── ExhibitionErrorCode.java
│   │       │   │
│   │       │   └── infrastructure
│   │       │       ├── persistence
│   │       │       │   ├── ExhibitionJpaEntity.java
│   │       │       │   ├── ExhibitionJpaRepository.java
│   │       │       │   ├── ExhibitionPersistenceAdapter.java
│   │       │       │   ├── ExhibitionPersistenceMapper.java
│   │       │       │   └── ExhibitionQueryRepositoryAdapter.java
│   │       │       └── client
│   │       │           └── ExternalExhibitionApiClient.java
│   │       │
│   │       └── global
│   │           ├── config
│   │           ├── error
│   │           ├── response
│   │           └── util
│   │
│   └── resources
│       ├── application.yaml
│       ├── application-local.yaml
│       └── application-dev.yaml
│
└── test
    ├── java
    │   └── com.displayu.backend
    └── resources
        └── application-test.yaml
```

## 📦 Package Convention

각 도메인은 다음과 같은 구조를 가집니다.

| Package | Description |
| --- | --- |
| `presentation` | Controller, Request, Response |
| `application` | UseCase, Service, Command, Result |
| `domain` | Entity, ValueObject, Domain Policy, Domain ErrorCode |
| `infrastructure` | JpaEntity, JpaRepository, 외부 API Client, Persistence Adapter |

예시:

```text
exhibition
├── presentation
├── application
├── domain
└── infrastructure
```

## ✅ Requirements

로컬 실행을 위해 아래 환경이 필요합니다.

- Java 21
- Docker
- Docker Compose

## 🚀 Getting Started

### 1. Clone Repository

```bash
git clone https://github.com/UMC-DISPLAYU/Backend.git
cd Backend
```

### 2. Start Local Database

로컬 개발용 MySQL은 Docker Compose로 실행합니다.

```bash
docker compose up -d
```

기본 데이터베이스 설정은 다음과 같습니다.

| Name | Value |
| --- | --- |
| Host | `localhost` |
| Port | `3306` |
| Database | `DisplayU` |
| Username | `DisplayUser` |

### 3. Run Application

로컬 환경에서는 `local` profile을 사용합니다.

```bash
./gradlew bootRun
```

profile을 명시하고 싶은 경우 아래 명령어를 사용할 수 있습니다.

```bash
./gradlew bootRun -Dspring.profiles.active=local
```

### 4. Check Server

서버가 정상적으로 실행되면 아래 주소에서 Swagger UI를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

Health Check API는 다음과 같습니다.

```http
GET /api/v1/health
```

## ⚙️ Profiles

| Profile | Config File | Description |
| --- | --- | --- |
| common | `application.yaml` | 공통 설정 |
| local | `application-local.yaml` | 로컬 개발 환경 |
| dev | `application-dev.yaml` | 개발 서버 환경 |
| test | `application-test.yaml` | 테스트 환경 |

서버 환경에서는 반드시 활성 profile을 명시해야 합니다.

```bash
SPRING_PROFILES_ACTIVE=dev java -jar app.jar
```

## 🔐 Environment Variables

`dev` profile에서는 아래 환경변수를 사용합니다.

| Name | Description | Default |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | 활성 Spring profile | required |
| `DB_HOST` | MySQL host | `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_NAME` | MySQL database name | `DisplayU` |
| `DB_USERNAME` | MySQL username | `DisplayUser` |
| `DB_PASSWORD` | MySQL password | local default only |

개발 서버 또는 운영 서버에서는 DB 비밀번호를 코드에 직접 작성하지 않고, 환경변수 또는 Secret Manager를 통해 주입합니다.

## 📚 API Documentation

애플리케이션 실행 후 Swagger UI에서 API 문서를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON 문서는 아래 주소에서 확인할 수 있습니다.

```text
http://localhost:8080/v3/api-docs
```

## 🩺 Health Check

서버 상태 확인 API입니다.

```http
GET /api/v1/health
```

Response:

```json
{
  "resultType": "SUCCESS",
  "success": {
    "data": {
      "status": "UP",
      "checkedAt": "2026-07-02T03:00:00"
    }
  },
  "error": null,
  "meta": {
    "timestamp": "2026-07-02T03:00:00",
    "path": "/api/v1/health"
  }
}
```

## 📄 Common Response Format

DisplayU Backend는 공통 응답 형식을 사용합니다.

### Success

```json
{
  "resultType": "SUCCESS",
  "success": {
    "data": {}
  },
  "error": null,
  "meta": {
    "timestamp": "2026-07-02T03:00:00",
    "path": "/api/v1/example"
  }
}
```

### Fail

```json
{
  "resultType": "FAIL",
  "success": null,
  "error": {
    "code": "INVALID_REQUEST",
    "message": "잘못된 요청입니다.",
    "details": null
  },
  "meta": {
    "timestamp": "2026-07-02T03:00:00",
    "path": "/api/v1/example"
  }
}
```

## 🧪 Test

테스트는 H2 in-memory database 기반으로 실행됩니다.

```bash
./gradlew test
```

전체 빌드는 다음 명령어로 실행합니다.

```bash
./gradlew clean build
```

## 🎨 Code Style

DisplayU Backend는 Spotless와 Google Java Format을 사용합니다.

코드 스타일 검사:

```bash
./gradlew spotlessCheck
```

코드 스타일 자동 적용:

```bash
./gradlew spotlessApply
```

`test` task는 `spotlessCheck`에 의존하므로, 테스트 실행 전 코드 스타일 검사가 함께 수행됩니다.

## 🔄 CI

GitHub Actions CI는 `dev`, `main` 브랜치에 대한 Pull Request와 Push에서 실행됩니다.

CI에서 검증하는 항목은 다음과 같습니다.

- PR 브랜치 네이밍 검사
- Java 21 환경 설정
- Spotless 코드 스타일 검사
- Gradle build
- H2 기반 테스트
- 테스트 리포트 업로드

## 🌿 Branch Naming Convention

브랜치 이름은 아래 규칙을 따릅니다.

```text
{type}/DU-{number}
```

예시는 다음과 같습니다.

```text
feat/DU-4
fix/DU-12
refactor/DU-7
```

허용되는 type은 다음과 같습니다.

```text
feat, fix, refactor, chore, docs, test, hotfix, release
```

## 🧾 Useful Commands

```bash
# Start local database
docker compose up -d

# Stop local database
docker compose down

# Run application
./gradlew bootRun

# Run tests
./gradlew test

# Build project
./gradlew clean build

# Apply code style
./gradlew spotlessApply

# Check code style
./gradlew spotlessCheck
```

## 🚢 Deployment

배포는 Docker image 기반으로 진행할 예정입니다.

```text
Build Application
    ↓
Build Docker Image
    ↓
Push Image to Registry
    ↓
Pull Image on Server
    ↓
Run Application Container
```

배포 관련 세부 설정은 추후 인프라 구성에 맞춰 업데이트합니다.

## 👥 Team

DisplayU Backend Team
