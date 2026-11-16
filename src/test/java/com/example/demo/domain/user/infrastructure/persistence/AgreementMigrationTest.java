package com.example.demo.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.type.Type;
import com.example.demo.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
@TestPropertySource(
    properties =
        "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl")
class AgreementMigrationTest {

  private static final String TERMS_CONTENT =
      String.join(
          "\n",
          "서비스 이용약관",
          "시행일: 2026년 11월 21일",
          "제1장 총칙",
          "제1조 (목적)",
          "이 약관은 디유 운영팀(이하 \"운영자\")이 제공하는 디유(displayU) 서비스를 이용할 때 운영자와 회원 사이의 권리, 의무, 책임과 이용 절차를 정합니다.",
          "제2조 (용어)",
          "이 약관에서 쓰는 말의 뜻은 다음과 같습니다.",
          "1. \"서비스\"는 운영자가 제공하는 디유와 그 안의 기능을 말합니다. 대학생 전시와 작품을 찾아보고, 감상하고, 기록하고, 전시를 만들어 알릴 수 있는 웹 서비스입니다.",
          "2. \"회원\"은 이 약관에 동의하고 가입해 서비스를 이용하는 사람을 말합니다.",
          "3. \"작가 회원\"은 학교 이메일로 작가 인증을 마친 회원을 말합니다.",
          "제3조 (약관의 게시와 변경)",
          "① 운영자는 이 약관을 가입 화면과 설정 화면에 게시해 누구나 볼 수 있게 합니다.",
          "② 운영자는 관련 법령을 어기지 않는 범위에서 이 약관을 바꿀 수 있습니다.");

  private static final String PRIVACY_COLLECTION_CONTENT =
      String.join(
          "\n",
          "개인정보 수집·이용 안내",
          "시행일: 2026년 11월 21일",
          "디유 운영팀(이하 \"운영자\")는 디유 가입과 서비스 이용에 필요한 만큼의 개인정보를 아래와 같이 수집하고 이용합니다. 자세한 내용은 개인정보 처리방침에서 볼 수 있습니다.",
          "1. 수집·이용하는 정보",
          "[가입할 때 (모든 회원)]",
          "목적: 회원 확인, 로그인, 중복 가입 확인, 서비스 안에서 이름 표시",
          "항목: 카카오 회원번호 또는 Google 계정 식별값, 닉네임, 만 14세 이상 확인 여부, 약관 동의 기록",
          "보유 기간: 탈퇴할 때까지",
          "[서비스를 이용할 때]",
          "목적: 저장·좋아요, 후기·방명록·댓글, 알림, 신고, 전시 참여 기능 제공",
          "항목: 저장·좋아요 기록, 작성한 후기·방명록·댓글(사진·그림 포함), 신고 기록, 알림, 받은 초대, 참여한 전시와 역할, 전시 작가명",
          "보유 기간: 탈퇴할 때까지 (후기·방명록·댓글은 아래 4번처럼 남음)",
          "[작가 인증할 때 (인증하는 회원만)]",
          "목적: 대학생 작가 확인, 한 학교 이메일을 한 계정만 쓰도록 확인, 작가 프로필 제공",
          "항목: 학교명, 학교 이메일, 인증 일시, 작가 프로필, 작가 분야",
          "보유 기간: 탈퇴할 때까지 (서버에 저장한 인증번호는 확인이 끝나면 바로 지움)");

  private static final String PRIVACY_POLICY_CONTENT =
      String.join(
          "\n",
          "개인정보 처리방침",
          "시행일: 2026년 11월 21일",
          "디유 운영팀(이하 \"운영자\")은 디유(displayU)를 이용하는 사람의 개인정보를 보호하고, 이와 관련한 고충을 신속하게 처리하기 위해 이 처리방침을 둡니다.",
          "1. 처리하는 개인정보와 목적",
          "디유는 아래 정보를 동의 없이, 회원과 맺은 이용계약을 이행하기 위해 처리합니다. (개인정보 보호법 제15조 제1항 제4호). 따로 동의를 받아 처리하는 정보는 없습니다.",
          "[가입할 때]",
          "목적: 회원 확인, 로그인, 중복 가입 확인, 서비스 안에서 이름 표시",
          "항목: 카카오 회원번호 또는 Google 계정 식별값, 닉네임, 만 14세 이상 확인 여부, 약관 동의 기록",
          "[서비스를 이용할 때]",
          "목적: 저장·좋아요, 후기·방명록·댓글, 알림, 신고, 전시 참여 기능 제공",
          "항목: 저장·좋아요 기록, 작성한 후기·방명록·댓글(사진·그림 포함), 신고 기록, 알림, 받은 초대, 참여한 전시와 역할, 전시 작가명",
          "[작가 인증할 때]",
          "목적: 대학생 작가 확인, 한 학교 이메일을 한 계정만 쓰도록 확인, 작가 프로필 제공",
          "항목: 학교명, 학교 이메일, 인증 일시, 작가 프로필, 작가 분야");

  @Autowired private EntityManager entityManager;
  @Autowired private DataSource dataSource;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  void insertsExactDocumentsAndPreservesExistingAgreementHistory() {
    Agreement legacyAgreement =
        Agreement.builder()
            .title("기존 동의 약관")
            .type(Type.SERVICE)
            .content("기존 내용")
            .isRequired(true)
            .code("LEGACY_TERMS_OF_SERVICE")
            .version("1.0")
            .isActive(false)
            .effectiveDate(LocalDate.of(2026, 8, 1))
            .displayOrder(100)
            .build();
    entityManager.persist(legacyAgreement);
    entityManager.flush();

    jdbcTemplate.execute(
        "ALTER TABLE Agreement ALTER COLUMN createdAt SET DEFAULT CURRENT_TIMESTAMP");
    jdbcTemplate.execute(
        "ALTER TABLE Agreement ALTER COLUMN updatedAt SET DEFAULT CURRENT_TIMESTAMP");
    new ResourceDatabasePopulator(
            new ClassPathResource("db/migration/V20261116000100__add_simplified_agreements.sql"))
        .execute(dataSource);

    assertAgreement("TERMS_OF_SERVICE", "서비스 이용약관", "SERVICE", true, "2.0", 1, TERMS_CONTENT);
    assertAgreement(
        "PRIVACY_COLLECTION_USE",
        "개인정보 수집·이용 안내",
        "PRIVACY",
        false,
        "2.0",
        2,
        PRIVACY_COLLECTION_CONTENT);
    assertAgreement(
        "PRIVACY_POLICY", "개인정보 처리방침", "PRIVACY", false, "1.0", 3, PRIVACY_POLICY_CONTENT);

    assertThat(
            jdbcTemplate.queryForMap(
                "SELECT code, version, content FROM Agreement WHERE agreeId = ?",
                legacyAgreement.getId()))
        .containsEntry("code", "LEGACY_TERMS_OF_SERVICE")
        .containsEntry("version", "1.0")
        .containsEntry("content", "기존 내용");
  }

  private void assertAgreement(
      String code,
      String title,
      String type,
      boolean required,
      String version,
      int displayOrder,
      String content) {
    List<AgreementRow> rows =
        jdbcTemplate.query(
            """
            SELECT title, type, content, isRequired, version, isActive, effectiveDate, displayOrder
            FROM Agreement
            WHERE code = ? AND version = ?
            """,
            (resultSet, rowNumber) ->
                new AgreementRow(
                    resultSet.getString("title"),
                    resultSet.getString("type"),
                    resultSet.getString("content"),
                    resultSet.getBoolean("isRequired"),
                    resultSet.getBoolean("isActive"),
                    resultSet.getObject("effectiveDate", LocalDate.class),
                    resultSet.getInt("displayOrder")),
            code,
            version);

    assertThat(rows)
        .containsExactly(
            new AgreementRow(
                title, type, content, required, true, LocalDate.of(2026, 11, 21), displayOrder));
  }

  private record AgreementRow(
      String title,
      String type,
      String content,
      boolean required,
      boolean active,
      LocalDate effectiveDate,
      int displayOrder) {}
}
