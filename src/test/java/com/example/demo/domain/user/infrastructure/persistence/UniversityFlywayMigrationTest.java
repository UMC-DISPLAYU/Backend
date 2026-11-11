package com.example.demo.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

class UniversityFlywayMigrationTest {

  private static final String URL =
      "jdbc:h2:mem:university_flyway;MODE=MySQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1";

  @Test
  void migratesLegacyUniversityDataAndBackfillsRelationships() throws Exception {
    createLegacyTablesAndData();

    Flyway flyway =
        Flyway.configure()
            .dataSource(URL, "sa", "")
            .baselineOnMigrate(true)
            .baselineVersion("20261111000200")
            .target("20261111000500")
            .locations("classpath:db/migration")
            .load();

    assertThat(flyway.migrate().migrationsExecuted).isEqualTo(3);

    try (Connection connection = DriverManager.getConnection(URL, "sa", "");
        Statement statement = connection.createStatement()) {
      assertThat(
              statement
                  .executeQuery("SELECT COUNT(*) FROM UniversityAlias WHERE aliasName = '안동대학교'")
                  .next())
          .isTrue();
      assertThat(queryLong(statement, "SELECT COUNT(*) FROM University")).isEqualTo(428L);
      assertThat(queryLong(statement, "SELECT COUNT(*) FROM UniversityAlias")).isEqualTo(21L);
      assertThat(queryLong(statement, "SELECT COUNT(*) FROM UniversityEmailDomain"))
          .isEqualTo(439L);
      assertThat(
              queryLong(
                  statement,
                  "SELECT COUNT(*) FROM UniversityEmailDomain d "
                      + "JOIN University u ON u.universityId = d.universityId "
                      + "WHERE u.name = '국립경국대학교' AND d.domain IN "
                      + "('student.gknu.ac.kr', 'student.anu.ac.kr')"))
          .isEqualTo(2L);
      assertThat(queryLong(statement, "SELECT universityId FROM User WHERE univName = '안동대학교'"))
          .isPositive();
      assertThat(
              queryLong(
                  statement,
                  "SELECT universityId FROM SchoolEmailVerification "
                      + "WHERE univName = '한국산업기술대학교'"))
          .isPositive();
    }
  }

  private void createLegacyTablesAndData() throws Exception {
    try (Connection connection = DriverManager.getConnection(URL, "sa", "");
        Statement statement = connection.createStatement()) {
      statement.execute(
          "CREATE TABLE User (userId BIGINT AUTO_INCREMENT PRIMARY KEY, univName VARCHAR(255))");
      statement.execute(
          "CREATE TABLE SchoolEmailVerification "
              + "(verificationId BIGINT AUTO_INCREMENT PRIMARY KEY, univName VARCHAR(255) NOT NULL)");
      statement.execute("INSERT INTO User (univName) VALUES ('안동대학교')");
      statement.execute("INSERT INTO SchoolEmailVerification (univName) VALUES ('한국산업기술대학교')");
    }
  }

  private long queryLong(Statement statement, String sql) throws Exception {
    try (var resultSet = statement.executeQuery(sql)) {
      resultSet.next();
      return resultSet.getLong(1);
    }
  }
}
