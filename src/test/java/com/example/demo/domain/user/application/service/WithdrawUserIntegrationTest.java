package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.infrastructure.persistence.SpringDataArchiveArtistJpaRepository;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.infrastructure.persistence.ArtistProfileJpaRepository;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.CreatorRole;
import com.example.demo.domain.displayartwork.infrastructure.persistence.SpringDataDisplayArtworkJpaRepository;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
  @Autowired private SpringDataDisplayJpaRepository displayRepository;
  @Autowired private SpringDataDisplayArtworkJpaRepository artworkRepository;
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

  @Test
  void leaderOfDeletedDisplayCanWithdraw() {
    createMigrationOnlyLikeTables();
    User user = saveUser(UUID.randomUUID().toString(), null);
    Display deletedDisplay = displayWithLeader(user.getId(), "deleted-display");
    deletedDisplay.delete();
    displayRepository.saveAndFlush(deletedDisplay);

    withdrawUserService.execute(new WithdrawUserCommand(user.getId()));
    entityManager.flush();
    entityManager.clear();

    assertThat(userRepository.findById(user.getId()).orElseThrow().getDeletedAt()).isNotNull();
  }

  @Test
  void activeDisplayLeaderGetsConflictEvenWhenAnotherLedDisplayIsDeleted() throws Exception {
    User user = saveUser(UUID.randomUUID().toString(), null);
    Display deletedDisplay = displayWithLeader(user.getId(), "deleted-display");
    deletedDisplay.delete();
    displayRepository.save(deletedDisplay);
    displayRepository.saveAndFlush(displayWithLeader(user.getId(), "active-display"));
    String accessToken = tokenProvider.createAccessToken(user);

    mockMvc
        .perform(delete("/api/v1/users/me").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_LEADER_REMOVAL_NOT_ALLOWED"));

    entityManager.clear();
    assertThat(userRepository.findById(user.getId()).orElseThrow().getDeletedAt()).isNull();
  }

  @Test
  void formerLeaderWithdrawalExitsOwnedDisplayAndCleansOnlyArtistLinkedArtworks() {
    createMigrationOnlyLikeTables();
    User formerLeader = saveUser(UUID.randomUUID().toString(), null);
    User currentLeader = saveUser(UUID.randomUUID().toString(), null);
    Display display = displayWithLeader(formerLeader.getId(), "transferred-display");
    display.addTeamMember(
        new TeamMember(
            null, new UserId(currentLeader.getId()), "현재 대표자", TeamMemberRole.MANAGER, true));
    display = displayRepository.saveAndFlush(display);
    display.transferLeadership(
        formerLeader.getId(), memberByUserId(display, currentLeader.getId()).getId());
    displayRepository.flush();

    DisplayArtwork sharedArtwork = persistArtwork(display, "공동 작품", currentLeader.getId(), 0);
    DisplayArtwork registeredOnly = persistArtwork(display, "대리 등록 작품", formerLeader.getId(), 1);
    DisplayArtwork currentLeaderArtwork =
        persistArtwork(display, "현재 대표자 작품", currentLeader.getId(), 2);
    persistCreator(sharedArtwork.getId(), "현재 대표자", CreatorRole.LEAD_ARTIST, currentLeader.getId());
    persistCreator(sharedArtwork.getId(), "이전 대표자", CreatorRole.CO_AUTHOR, formerLeader.getId());
    persistCreator(registeredOnly.getId(), "직접 입력 작가", CreatorRole.LEAD_ARTIST, null);
    persistCreator(
        currentLeaderArtwork.getId(), "현재 대표자", CreatorRole.LEAD_ARTIST, currentLeader.getId());

    withdrawUserService.execute(new WithdrawUserCommand(formerLeader.getId()));
    entityManager.flush();
    entityManager.clear();

    Display savedDisplay = displayRepository.findById(display.getId()).orElseThrow();
    assertThat(memberByUserId(savedDisplay, formerLeader.getId()).isDeleted()).isTrue();
    assertThat(memberByUserId(savedDisplay, currentLeader.getId()).isDeleted()).isFalse();
    assertThat(memberByUserId(savedDisplay, currentLeader.getId()).getRole())
        .isEqualTo(TeamMemberRole.TEAM_LEADER);
    assertThat(artworkRepository.findById(sharedArtwork.getId()).orElseThrow().isDeleted())
        .isTrue();
    assertThat(artworkRepository.findById(registeredOnly.getId()).orElseThrow().isDeleted())
        .isFalse();
    assertThat(artworkRepository.findById(currentLeaderArtwork.getId()).orElseThrow().isDeleted())
        .isFalse();
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

  private static Display displayWithLeader(Long ownerUserId, String title) {
    Display display =
        Display.create(
            new UserId(ownerUserId),
            title,
            "https://cdn.displayu.com/poster.png",
            "subtitle",
            "content",
            new DisplayLocation("전시장", BigDecimal.ONE, BigDecimal.ONE),
            "",
            "",
            "organization",
            "department",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 2),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.IMMEDIATELY);
    display.addTeamMember(
        new TeamMember(null, new UserId(ownerUserId), "대표자", TeamMemberRole.TEAM_LEADER, true));
    return display;
  }

  private DisplayArtwork persistArtwork(
      Display display, String artworkName, Long registeredByUserId, int sortOrder) {
    DisplayArtwork artwork =
        DisplayArtwork.create(
            display,
            artworkName,
            "작품 설명",
            List.of(ArtworkType.PAINTING),
            2026,
            "재료",
            "10 x 10 cm",
            "감상 포인트",
            sortOrder,
            registeredByUserId,
            List.of(
                new ArtworkImage(
                    null,
                    "https://cdn.displayu.com/artworks/" + sortOrder + ".png",
                    true,
                    ArtworkImageType.ARTWORK,
                    0,
                    null,
                    100,
                    100)));
    return artworkRepository.saveAndFlush(artwork);
  }

  private void persistCreator(Long artworkId, String name, CreatorRole role, Long userId) {
    entityManager
        .createNativeQuery(
            """
            INSERT INTO Creator
              (creatorId, displayArtworkId, creatorName, isContact, isLeader, role, userId)
            VALUES (?, ?, ?, FALSE, ?, ?, ?)
            """)
        .setParameter(1, artworkId * 10 + role.ordinal())
        .setParameter(2, artworkId)
        .setParameter(3, name)
        .setParameter(4, role == CreatorRole.LEAD_ARTIST)
        .setParameter(5, role.name())
        .setParameter(6, userId)
        .executeUpdate();
    entityManager.flush();
    entityManager.clear();
  }

  private static TeamMember memberByUserId(Display display, Long userId) {
    return display.getTeamMembers().stream()
        .filter(member -> member.getUserId().value().equals(userId))
        .findFirst()
        .orElseThrow();
  }
}
