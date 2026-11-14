package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.infrastructure.persistence.SpringDataArchiveArtistJpaRepository;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.infrastructure.persistence.ArtistProfileJpaRepository;
import com.example.demo.domain.user.application.command.WithdrawUserCommand;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.entity.RefreshToken;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.infrastructure.persistence.RefreshTokenJpaRepository;
import com.example.demo.domain.user.infrastructure.persistence.SchoolEmailVerificationJpaRepository;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.TokenProvider;
import jakarta.persistence.EntityManager;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:withdraw_user_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
      "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WithdrawUserIntegrationTest {

  @Autowired private WithdrawUserService withdrawUserService;
  @Autowired private UserJpaRepository userRepository;
  @Autowired private RefreshTokenJpaRepository refreshTokenRepository;
  @Autowired private SchoolEmailVerificationJpaRepository verificationRepository;
  @Autowired private ArtistProfileJpaRepository artistProfileRepository;
  @Autowired private SpringDataArchiveArtistJpaRepository archiveArtistRepository;
  @Autowired private TokenProvider tokenProvider;
  @Autowired private MockMvc mockMvc;
  @Autowired private EntityManager entityManager;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  void withdrawsImmediatelyAndReleasesSocialAndSchoolIdentities() throws Exception {
    createMigrationOnlyLikeTables();
    String providerId = UUID.randomUUID().toString();
    String schoolEmail = providerId + "@university.ac.kr";
    User user = saveUser(providerId, schoolEmail);
    User archiveOwner = saveUser(UUID.randomUUID().toString(), null);
    String accessToken = tokenProvider.createAccessToken(user);
    String refreshToken = tokenProvider.createRefreshToken(user);
    refreshTokenRepository.save(
        RefreshToken.builder().user(user).refreshToken(refreshToken).build());
    verificationRepository.save(SchoolEmailVerification.create(user, schoolEmail, "대학교", "123456"));
    ArtistProfile artistProfile =
        artistProfileRepository.save(ArtistProfile.create(user, "탈퇴작가", schoolEmail, "대학교", null));
    archiveArtistRepository.save(
        ArchiveArtist.create(artistProfile.getId(), user.getId(), archiveOwner.getId()));
    entityManager.flush();

    withdrawUserService.execute(new WithdrawUserCommand(user.getId()));
    entityManager.clear();

    User withdrawn = userRepository.findById(user.getId()).orElseThrow();
    assertThat(withdrawn.getDeletedAt()).isNotNull();
    assertThat(withdrawn.getName()).isEqualTo(User.WITHDRAWN_DISPLAY_NAME);
    assertThat(withdrawn.getNickname()).isEqualTo(User.WITHDRAWN_DISPLAY_NAME);
    assertThat(withdrawn.getProfileImageUrl()).isNull();
    assertThat(withdrawn.getSchoolEmail()).isNull();
    assertThat(withdrawn.isVerified()).isFalse();
    assertThat(userRepository.findByProviderAndProviderId(Provider.Kakao, providerId)).isEmpty();
    assertThat(userRepository.existsBySchoolEmail(schoolEmail)).isFalse();
    assertThat(refreshTokenRepository.findByUserId(user.getId())).isEmpty();
    assertThat(verificationRepository.findAll()).isEmpty();
    assertThat(artistProfileRepository.findByUserId(user.getId())).isEmpty();
    assertThat(archiveArtistRepository.findAll()).isEmpty();

    mockMvc
        .perform(get("/api/v1/users/me").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isUnauthorized());
  }

  private void createMigrationOnlyLikeTables() {
    for (String tableName :
        Set.of(
            "ArtworkQuestionLike",
            "ArtworkQuestionReplyLike",
            "PersonalArtworkQuestionLike",
            "PersonalArtworkQuestionReplyLike")) {
      jdbcTemplate.execute(
          "CREATE TABLE IF NOT EXISTS " + tableName + " (id BIGINT AUTO_INCREMENT, userId BIGINT)");
    }
  }

  private User saveUser(String providerId, String schoolEmail) {
    return userRepository.saveAndFlush(
        User.builder()
            .provider(Provider.Kakao)
            .providerId(providerId)
            .name("사용자")
            .nickname("user-" + providerId)
            .profileImageUrl("https://example.com/profile.jpg")
            .socialEmail(providerId + "@example.com")
            .schoolEmail(schoolEmail)
            .univName(schoolEmail == null ? null : "대학교")
            .isVerified(schoolEmail != null)
            .build());
  }
}
