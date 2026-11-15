package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import com.example.demo.domain.admin.application.port.UserStatusPort;
import com.example.demo.domain.admin.application.service.AdminUserStatusService;
import com.example.demo.domain.user.application.command.ChangeNicknameCommand;
import com.example.demo.domain.user.application.command.UpdateMyProfileCommand;
import com.example.demo.domain.user.application.service.ChangeNicknameService;
import com.example.demo.domain.user.application.service.UpdateMyProfileService;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.type.AccountStatus;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.domain.user.domain.vo.ProfileImageUrl;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.TokenProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(properties = {"admin.user-status.enabled=true", "spring.jpa.open-in-view=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserStatusConcurrencyIntegrationTest {

  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository userJpaRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private UserStatusPort userStatusPort;
  @Autowired private AdminUserStatusService adminUserStatusService;
  @Autowired private UpdateMyProfileService updateMyProfileService;
  @Autowired private ChangeNicknameService changeNicknameService;
  @Autowired private PlatformTransactionManager transactionManager;
  @Autowired private EntityManagerFactory entityManagerFactory;

  private final ExecutorService executor = Executors.newFixedThreadPool(2);
  private final List<Long> createdUserIds = new java.util.concurrent.CopyOnWriteArrayList<>();

  @AfterEach
  void tearDown() throws Exception {
    executor.shutdownNow();
    executor.awaitTermination(5, TimeUnit.SECONDS);
    userJpaRepository.deleteAllById(createdUserIds);
    userJpaRepository.flush();
  }

  @Test
  void concurrentSuspensionsReturnOneSuccessAndOneConflictWithoutOverwritingWinner()
      throws Exception {
    User firstAdmin = user(UserRole.ADMIN, "firstAdmin");
    User secondAdmin = user(UserRole.ADMIN, "secondAdmin");
    User target = user(UserRole.USER, "suspensionTarget");
    CountDownLatch start = new CountDownLatch(1);

    Future<HttpChangeResult> first =
        executor.submit(() -> changeStatus(start, firstAdmin, target, "SUSPENDED", "첫 번째 사유"));
    Future<HttpChangeResult> second =
        executor.submit(() -> changeStatus(start, secondAdmin, target, "SUSPENDED", "두 번째 사유"));
    start.countDown();

    HttpChangeResult firstResult = first.get(10, TimeUnit.SECONDS);
    HttpChangeResult secondResult = second.get(10, TimeUnit.SECONDS);
    assertOneSuccessAndOneConflict(
        firstResult, secondResult, UserErrorCode.ALREADY_SUSPENDED_USER.getCode());

    HttpChangeResult winner = firstResult.status() == 200 ? firstResult : secondResult;
    User saved = findFresh(target.getId());
    assertThat(saved.getAccountStatus()).isEqualTo(AccountStatus.SUSPENDED);
    assertThat(saved.getSuspendedAt()).isNotNull();
    assertThat(saved.getSuspensionOperatorId()).isEqualTo(winner.operatorId());
    assertThat(saved.getSuspensionReason()).isEqualTo(winner.reason());
  }

  @Test
  void concurrentReleasesReturnOneSuccessAndOneConflict() throws Exception {
    User firstAdmin = user(UserRole.ADMIN, "releaseAdmin1");
    User secondAdmin = user(UserRole.ADMIN, "releaseAdmin2");
    User target = user(UserRole.USER, "releaseTarget");
    suspend(target, firstAdmin, "기존 정지 사유");
    CountDownLatch start = new CountDownLatch(1);

    Future<HttpChangeResult> first =
        executor.submit(() -> changeStatus(start, firstAdmin, target, "ACTIVE", "첫 번째 해제"));
    Future<HttpChangeResult> second =
        executor.submit(() -> changeStatus(start, secondAdmin, target, "ACTIVE", "두 번째 해제"));
    start.countDown();

    HttpChangeResult firstResult = first.get(10, TimeUnit.SECONDS);
    HttpChangeResult secondResult = second.get(10, TimeUnit.SECONDS);
    assertOneSuccessAndOneConflict(
        firstResult, secondResult, UserErrorCode.ALREADY_ACTIVE_USER.getCode());
    assertThat(findFresh(target.getId()).getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
  }

  @Test
  void profileUpdateAndSuspensionBothRemainAfterSerializedRowLocks() throws Exception {
    User admin = user(UserRole.ADMIN, "profileAdmin");
    User target = user(UserRole.USER, "oldProfile");

    runUserChangeWhileSuspensionWaits(
        target,
        admin,
        () ->
            updateMyProfileService.execute(
                new UpdateMyProfileCommand(
                    target.getId(),
                    ProfileImageUrl.ofNullable(
                        "https://d1tdgnysscm2va.cloudfront.net/images/user/new-profile.jpg"),
                    Nickname.of("newProfile"))));

    User saved = findFresh(target.getId());
    assertSuspendedBy(saved, admin, "동시 정지");
    assertThat(saved.getNickname()).isEqualTo("newProfile");
    assertThat(saved.getProfileImageUrl())
        .isEqualTo("https://d1tdgnysscm2va.cloudfront.net/images/user/new-profile.jpg");
  }

  @Test
  void nicknameChangeAndSuspensionBothRemainAfterSerializedRowLocks() throws Exception {
    User admin = user(UserRole.ADMIN, "nicknameAdmin");
    User target = user(UserRole.USER, "oldNickname");

    runUserChangeWhileSuspensionWaits(
        target,
        admin,
        () ->
            changeNicknameService.execute(
                new ChangeNicknameCommand(target.getId(), Nickname.of("newNickname"))));

    User saved = findFresh(target.getId());
    assertSuspendedBy(saved, admin, "동시 정지");
    assertThat(saved.getNickname()).isEqualTo("newNickname");
  }

  @Test
  void staleRequestPersistenceContextIsRefreshedAfterPessimisticLock() throws Exception {
    User admin = user(UserRole.ADMIN, "osivAdmin");
    User target = user(UserRole.USER, "osivTarget");
    EntityManager requestEntityManager = entityManagerFactory.createEntityManager();
    EntityManagerHolder holder = new EntityManagerHolder(requestEntityManager);
    TransactionSynchronizationManager.bindResource(entityManagerFactory, holder);

    try {
      User stale = requestEntityManager.find(User.class, target.getId());
      assertThat(stale.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);

      executor
          .submit(
              () -> {
                suspend(target, admin, "최초 정지 사유");
                return null;
              })
          .get(10, TimeUnit.SECONDS);

      assertThat(stale.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
      assertThatExceptionOfType(UserException.class)
          .isThrownBy(
              () ->
                  adminUserStatusService.changeStatus(
                      admin.getId(), target.getId(), "SUSPENDED", "덮어쓸 사유"))
          .satisfies(
              exception ->
                  assertThat(exception.errorCode())
                      .isEqualTo(UserErrorCode.ALREADY_SUSPENDED_USER));

      User saved = findFresh(target.getId());
      assertSuspendedBy(saved, admin, "최초 정지 사유");
      assertThat(UserErrorCode.ALREADY_SUSPENDED_USER.getStatus().value()).isEqualTo(409);
    } finally {
      TransactionSynchronizationManager.unbindResource(entityManagerFactory);
      requestEntityManager.close();
    }
  }

  private void runUserChangeWhileSuspensionWaits(User target, User admin, Runnable userChange)
      throws Exception {
    CountDownLatch suspensionStarted = new CountDownLatch(1);
    @SuppressWarnings("unchecked")
    Future<Void>[] suspension = new Future[1];

    new TransactionTemplate(transactionManager)
        .executeWithoutResult(
            status -> {
              userRepository.findByIdForUpdate(target.getId()).orElseThrow();
              suspension[0] =
                  executor.submit(
                      () -> {
                        suspensionStarted.countDown();
                        userStatusPort.changeStatus(
                            target.getId(), admin.getId(), "SUSPENDED", "동시 정지");
                        return null;
                      });
              await(suspensionStarted);
              userChange.run();
            });

    suspension[0].get(10, TimeUnit.SECONDS);
  }

  private HttpChangeResult changeStatus(
      CountDownLatch start, User admin, User target, String status, String reason)
      throws Exception {
    await(start);
    MvcResult result =
        mvc.perform(
                patch("/api/v1/admin/users/{userId}/account-status", target.getId())
                    .header("Authorization", "Bearer " + tokens.createAccessToken(admin))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"accountStatus\":\"" + status + "\",\"reason\":\"" + reason + "\"}"))
            .andReturn();
    return new HttpChangeResult(
        result.getResponse().getStatus(),
        result.getResponse().getContentAsString(),
        admin.getId(),
        reason);
  }

  private void assertOneSuccessAndOneConflict(
      HttpChangeResult first, HttpChangeResult second, String expectedErrorCode) {
    assertThat(List.of(first.status(), second.status())).containsExactlyInAnyOrder(200, 409);
    HttpChangeResult conflict = first.status() == 409 ? first : second;
    assertThat(conflict.body()).contains("\"code\":\"" + expectedErrorCode + "\"");
  }

  private void suspend(User target, User admin, String reason) {
    new TransactionTemplate(transactionManager)
        .executeWithoutResult(
            status ->
                userStatusPort.changeStatus(target.getId(), admin.getId(), "SUSPENDED", reason));
  }

  private User findFresh(Long userId) {
    return new TransactionTemplate(transactionManager)
        .execute(
            status -> {
              User user = userJpaRepository.findById(userId).orElseThrow();
              user.getAccountStatus();
              return user;
            });
  }

  private void assertSuspendedBy(User user, User admin, String reason) {
    assertThat(user.getAccountStatus()).isEqualTo(AccountStatus.SUSPENDED);
    assertThat(user.getSuspendedAt()).isNotNull();
    assertThat(user.getSuspensionOperatorId()).isEqualTo(admin.getId());
    assertThat(user.getSuspensionReason()).isEqualTo(reason);
  }

  private User user(UserRole role, String nickname) {
    String identifier = UUID.randomUUID().toString();
    User user =
        userJpaRepository.saveAndFlush(
            User.builder()
                .provider(Provider.Kakao)
                .providerId(identifier)
                .name("사용자")
                .nickname(nickname + identifier.substring(0, 8))
                .socialEmail(identifier + "@example.com")
                .role(role)
                .build());
    createdUserIds.add(user.getId());
    return user;
  }

  private static void await(CountDownLatch latch) {
    try {
      assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(exception);
    }
  }

  private record HttpChangeResult(int status, String body, Long operatorId, String reason) {}
}
