# Flyway Deployment Safety

## 왜 validate만으로 부족한가

`flyway validate`는 migration 파일의 checksum, 적용 순서, schema history와 파일 상태를 검증한다. 하지만 운영 DB의 실제 테이블, 컬럼, 인덱스, FK 이름이 migration 작성자가 예상한 상태와 다른지는 DDL을 실행하기 전까지 알 수 없다.

예를 들어 운영에는 `FK_ARCHIVEARTIST_CREATOR`가 없는데 신규 migration이 `DROP FOREIGN KEY FK_ARCHIVEARTIST_CREATOR`를 실행하면 validate는 통과할 수 있다. MySQL DDL은 많은 경우 트랜잭션으로 되돌릴 수 없으므로 운영에서 실패하면 `flyway_schema_history.success = 0`이 남을 수 있다.

## 운영 스키마 복제 테스트

CD의 `migration-precheck` job은 운영 DB에 신규 migration을 실행하지 않는다. 대신 다음 순서로 임시 MySQL에 운영 스키마 상태를 복제한 뒤 신규 migration을 실제 실행한다.

1. `mysqldump --no-data`로 운영 테이블, 컬럼, FK, UNIQUE, 인덱스, 생성 컬럼, 트리거, routine, event 정의를 추출한다.
2. 운영 `flyway_schema_history` 데이터만 별도로 추출한다.
3. 운영 실제 데이터 전체는 복사하지 않는다.
4. GitHub Actions에서 `MYSQL_IMAGE`로 지정한 운영 MySQL과 같은 버전의 임시 MySQL을 실행한다.
5. 임시 DB에 schema dump와 `flyway_schema_history`를 복원한다.
6. 임시 DB에서 `flyway validate`, `flyway migrate`를 실행한다.
7. migration 이후 Spring Boot를 `spring.flyway.enabled=false`, `spring.jpa.hibernate.ddl-auto=validate`로 기동해 Hibernate schema validation을 확인한다.

## Job 순서

CD는 다음 dependency로 고정한다.

- `build`
- `migration-precheck`: `build` 성공 후 실행
- `production-migrate`: `migration-precheck` 성공 후에만 실행
- `deploy`: `production-migrate` 성공 후에만 실행

`migration-precheck`가 실패하면 운영 Flyway migration과 새 애플리케이션 배포는 실행되지 않는다.

## success = 0 재발 방지 원리

운영 DB와 같은 schema history, 같은 현재 스키마를 가진 임시 DB에서 신규 migration을 먼저 실제 DDL로 실행한다. 따라서 존재하지 않는 FK/컬럼/인덱스 삭제, 이미 존재하는 컬럼/제약조건 추가, NOT NULL 변경 실패, UNIQUE 충돌, FK 생성 실패, MySQL 버전별 문법 오류를 운영 DB에 도달하기 전에 차단한다.

`FK_ARCHIVEARTIST_CREATOR`처럼 운영 실제 FK 이름과 migration SQL의 FK 이름이 다르면 임시 DB의 `flyway migrate` 단계에서 먼저 실패하고, `production-migrate`와 `deploy`는 실행되지 않는다.

## 접근 권한과 secret

기존 CD와 동일하게 GitHub Actions runner가 운영 DB에 접근한다. 운영 DB가 외부 runner에서 접근 불가능한 구조라면 DB를 public으로 열지 말고 VPC 내부 self-hosted runner, EC2 SSH 실행, AWS SSM Run Command, ECS one-off migration task 중 현재 인프라에 맞는 방식을 사용한다.

DB 비밀번호는 `MYSQL_PWD` 또는 Docker env로만 전달하고 로그에 출력하지 않는다. workflow는 DB host, port, name, username, password, JDBC URL을 Actions log mask에 등록한다.

## 금지 사항

일반 CD에는 다음을 자동화하지 않는다.

- `flyway repair`
- `flyway clean`
- 자동 `baseline`
- `flyway_schema_history` 직접 수정

운영 schema history가 깨졌다면 자동 복구하지 말고 원인 SQL, 실제 DB 상태, 적용 여부를 운영자가 확인한 뒤 수동 절차로 처리한다.

## 실패 시 확인 위치

Actions summary에서 다음 항목을 확인한다.

- `Migration risk scan`: 위험 DDL/DML 감지 파일과 라인
- `Migration precheck failed`: 실패 migration 파일명과 Flyway SQL 오류
- `Spring Boot schema validation failed`: Hibernate validate 또는 context startup 실패 로그
- `Production migration failed`: precheck 통과 이후 운영 migration에서 실패한 경우의 migration 파일명과 오류
