# Database Migration

이 프로젝트는 DB 스키마 변경 관리를 위해 Flyway를 사용한다.

## 위치

마이그레이션 SQL 파일은 아래 경로에 둔다.

```text
src/main/resources/db/migration
```

현재 초기 마이그레이션 파일은 다음과 같다.

```text
V1__init_schema.sql
```

## 파일명 규칙

Flyway 버전 마이그레이션 파일명은 아래 규칙을 따른다.

```text
V{버전}__{변경내용}.sql
```

- `V`는 대문자를 사용한다.
- 버전과 설명 사이에는 언더스코어 2개(`__`)를 사용한다.
- 설명은 영어 소문자와 언더스코어를 사용한다.
- 날짜는 파일명에 넣지 않는다. 적용 시간은 `flyway_schema_history`에 기록된다.

예시:

```text
V2__add_user_status.sql
V3__create_notification_table.sql
V4__alter_display_add_view_count.sql
```

## 실행

Spring Boot 애플리케이션 실행 시 Flyway가 자동으로 아직 적용되지 않은 마이그레이션을 실행한다.

```bash
./gradlew bootRun
```

로컬 프로필을 명시하려면 다음처럼 실행한다.

```bash
./gradlew bootRun -Dspring.profiles.active=local
```

## 설정

`local`, `dev` 환경에서는 Hibernate가 스키마를 자동 변경하지 않고 검증만 하도록 설정한다.

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

스키마 변경은 `ddl-auto: update`가 아니라 Flyway SQL 파일로 관리한다.

