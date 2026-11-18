package com.example.demo.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import com.example.demo.domain.admin.application.port.UserQueryPort;
import com.example.demo.domain.admin.application.query.UserSearchQuery;
import com.example.demo.domain.admin.application.result.UserDetail;
import com.example.demo.domain.admin.application.result.UserPage;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.AccountStatus;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.global.config.JpaAuditingConfig;
import com.example.demo.global.config.QuerydslConfig;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest(
    properties =
        "spring.datasource.url=jdbc:h2:mem:admin_user_query;MODE=MySQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({AdminUserQueryAdapter.class, JpaAuditingConfig.class, QuerydslConfig.class})
class AdminUserQueryAdapterTest {

  @Autowired private UserQueryPort userQueries;
  @Autowired private UserJpaRepository users;

  @Test
  void searchesAllUsersInStableDescendingIdOrder() {
    User first = user("first", "first@example.com");
    User second = user("second", "second@example.com");
    User third = user("third", "third@example.com");

    UserPage result = search(null, null, null, 100);

    assertThat(result.items())
        .extracting(item -> item.userId())
        .containsExactly(third.getId(), second.getId(), first.getId());
    assertThat(result.items().getFirst().nickname()).isEqualTo("third");
    assertThat(result.items().getFirst().email()).isEqualTo("third@example.com");
    assertThat(result.items().getFirst().accountStatus()).isEqualTo("ACTIVE");
    assertThat(result.items().getFirst().createdAt())
        .isCloseTo(third.getCreatedAt().toInstant(ZoneOffset.UTC), within(1, ChronoUnit.MILLIS));
    assertThat(result.nextCursor()).isNull();
    assertThat(result.hasNext()).isFalse();
  }

  @Test
  void searchesNicknameAndEmailByTrimmedCaseInsensitivePartialMatch() {
    User nicknameMatch = user("CanvasArtist", "one@example.com");
    User emailMatch = user("second", "canvas@example.com");
    user("unrelated", "other@example.com");

    assertThat(search("  canvas  ", null, null, 100).items())
        .extracting(item -> item.userId())
        .containsExactly(emailMatch.getId(), nicknameMatch.getId());
  }

  @Test
  void combinesSearchAndAccountStatusFilter() {
    User active = user("artist-active", "active@example.com");
    User suspended = user("artist-suspended", "suspended@example.com");
    suspended.suspend(LocalDateTime.parse("2026-11-18T01:02:03"), 91L, "policy violation");
    users.flush();

    assertThat(search("artist", "ACTIVE", null, 100).items())
        .extracting(item -> item.userId())
        .containsExactly(active.getId());
    assertThat(search("artist", "SUSPENDED", null, 100).items())
        .extracting(item -> item.userId())
        .containsExactly(suspended.getId());
  }

  @Test
  void paginatesFirstNextAndLastPagesWithLastReturnedIdAsCursor() {
    User first = user("first", "first@example.com");
    User second = user("second", "second@example.com");
    User third = user("third", "third@example.com");
    User fourth = user("fourth", "fourth@example.com");
    User fifth = user("fifth", "fifth@example.com");

    UserPage firstPage = search(null, null, null, 2);
    UserPage nextPage = search(null, null, firstPage.nextCursor(), 2);
    UserPage lastPage = search(null, null, nextPage.nextCursor(), 2);

    assertThat(firstPage.items())
        .extracting(item -> item.userId())
        .containsExactly(fifth.getId(), fourth.getId());
    assertThat(firstPage.nextCursor()).isEqualTo(fourth.getId());
    assertThat(firstPage.hasNext()).isTrue();
    assertThat(nextPage.items())
        .extracting(item -> item.userId())
        .containsExactly(third.getId(), second.getId());
    assertThat(nextPage.nextCursor()).isEqualTo(second.getId());
    assertThat(nextPage.hasNext()).isTrue();
    assertThat(lastPage.items()).extracting(item -> item.userId()).containsExactly(first.getId());
    assertThat(lastPage.nextCursor()).isNull();
    assertThat(lastPage.hasNext()).isFalse();
    assertThat(search(null, null, first.getId(), 2).items()).isEmpty();
  }

  @Test
  void excludesWithdrawnUsersFromListAndDetailWithoutExposingAnonymizedIdentifiers() {
    User withdrawn = user("private-name", "private@example.com");
    withdrawn.withdraw(LocalDateTime.parse("2026-11-18T02:03:04"));
    users.flush();

    assertThat(search(null, null, null, 100).items()).isEmpty();
    assertThatThrownBy(() -> userQueries.getDetail(withdrawn.getId()))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(GlobalErrorCode.NOT_FOUND));
  }

  @Test
  void returnsActiveUserDetailWithEmptySuspensionDataAndHistory() {
    User active = user("active", "active@example.com");

    UserDetail result = userQueries.getDetail(active.getId());

    assertThat(result.userId()).isEqualTo(active.getId());
    assertThat(result.nickname()).isEqualTo("active");
    assertThat(result.email()).isEqualTo("active@example.com");
    assertThat(result.accountStatus()).isEqualTo("ACTIVE");
    assertThat(result.createdAt())
        .isCloseTo(active.getCreatedAt().toInstant(ZoneOffset.UTC), within(1, ChronoUnit.MILLIS));
    assertThat(result.suspendedAt()).isNull();
    assertThat(result.operatorId()).isNull();
    assertThat(result.reason()).isNull();
    assertThat(result.history()).isEmpty();
  }

  @Test
  void returnsActualSuspensionDataAndReflectsReleaseInListAndDetail() {
    User target = user("target", "target@example.com");
    LocalDateTime suspendedAt = LocalDateTime.parse("2026-11-18T03:04:05");
    target.suspend(suspendedAt, 77L, "policy violation");
    users.flush();

    UserDetail suspended = userQueries.getDetail(target.getId());
    assertThat(suspended.accountStatus()).isEqualTo("SUSPENDED");
    assertThat(suspended.suspendedAt()).isEqualTo(suspendedAt.toInstant(ZoneOffset.UTC));
    assertThat(suspended.operatorId()).isEqualTo(77L);
    assertThat(suspended.reason()).isEqualTo("policy violation");
    assertThat(suspended.history()).isEmpty();
    assertThat(search(null, "SUSPENDED", null, 100).items())
        .extracting(item -> item.userId())
        .containsExactly(target.getId());

    target.releaseSuspension();
    users.flush();

    UserDetail released = userQueries.getDetail(target.getId());
    assertThat(released.accountStatus()).isEqualTo("ACTIVE");
    assertThat(released.suspendedAt()).isNull();
    assertThat(released.operatorId()).isNull();
    assertThat(released.reason()).isNull();
    assertThat(search(null, "ACTIVE", null, 100).items())
        .extracting(item -> item.userId())
        .containsExactly(target.getId());
  }

  @Test
  void returnsGlobalNotFoundForUnknownUser() {
    assertThatThrownBy(() -> userQueries.getDetail(Long.MAX_VALUE))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(GlobalErrorCode.NOT_FOUND));
  }

  private UserPage search(String q, String accountStatus, Long cursor, int size) {
    return userQueries.search(new UserSearchQuery(q, accountStatus, cursor, size));
  }

  private User user(String nickname, String email) {
    return users.saveAndFlush(
        User.builder()
            .provider(Provider.Kakao)
            .providerId(nickname)
            .name(nickname)
            .nickname(nickname)
            .socialEmail(email)
            .accountStatus(AccountStatus.ACTIVE)
            .build());
  }
}
