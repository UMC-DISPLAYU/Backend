package com.example.demo.domain.display.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@ActiveProfiles("test")
class DisplayLeaderTransferConcurrencyTest {

  private static final Long LEADER_USER_ID = 201L;
  private static final Long FIRST_TARGET_USER_ID = 202L;
  private static final Long SECOND_TARGET_USER_ID = 203L;

  @Autowired private SpringDataDisplayJpaRepository displayRepository;
  @Autowired private PlatformTransactionManager transactionManager;

  private Long displayId;

  @AfterEach
  void tearDown() {
    if (displayId != null) {
      displayRepository.deleteById(displayId);
    }
  }

  @Test
  void concurrentLeaderTransfersKeepExactlyOneLeader() throws Exception {
    TransferFixture fixture = saveFixture();
    CyclicBarrier barrier = new CyclicBarrier(2);
    ExecutorService executor = Executors.newFixedThreadPool(2);

    try {
      Future<Boolean> first =
          executor.submit(() -> transferInTransaction(fixture.firstTargetMemberId(), barrier));
      Future<Boolean> second =
          executor.submit(() -> transferInTransaction(fixture.secondTargetMemberId(), barrier));

      long successCount =
          List.of(first, second).stream().filter(this::completedSuccessfully).count();
      long leaderCount =
          new TransactionTemplate(transactionManager)
              .execute(
                  status -> {
                    Display savedDisplay = displayRepository.findById(displayId).orElseThrow();
                    return savedDisplay.getTeamMembers().stream()
                        .filter(member -> member.isAccepted() && !member.isDeleted())
                        .filter(member -> member.getRole() == TeamMemberRole.TEAM_LEADER)
                        .count();
                  });

      assertThat(successCount).isEqualTo(1);
      assertThat(leaderCount).isEqualTo(1);
    } finally {
      executor.shutdownNow();
      executor.awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  private TransferFixture saveFixture() {
    return new TransactionTemplate(transactionManager)
        .execute(
            status -> {
              Display display = displayRepository.saveAndFlush(display());
              displayId = display.getId();
              return new TransferFixture(
                  memberByUserId(display, FIRST_TARGET_USER_ID).getId(),
                  memberByUserId(display, SECOND_TARGET_USER_ID).getId());
            });
  }

  private boolean transferInTransaction(Long targetMemberId, CyclicBarrier barrier) {
    try {
      Boolean result =
          new TransactionTemplate(transactionManager)
              .execute(
                  status -> {
                    Display display =
                        displayRepository.findWithOptimisticLockById(displayId).orElseThrow();
                    await(barrier);
                    display.transferLeadership(LEADER_USER_ID, targetMemberId);
                    displayRepository.flush();
                    return true;
                  });
      return Boolean.TRUE.equals(result);
    } catch (RuntimeException exception) {
      return false;
    }
  }

  private boolean completedSuccessfully(Future<Boolean> future) {
    try {
      return future.get(10, TimeUnit.SECONDS);
    } catch (ExecutionException exception) {
      return false;
    } catch (Exception exception) {
      throw new IllegalStateException(exception);
    }
  }

  private static void await(CyclicBarrier barrier) {
    try {
      barrier.await(5, TimeUnit.SECONDS);
    } catch (BrokenBarrierException exception) {
      throw new IllegalStateException(exception);
    } catch (Exception exception) {
      throw new IllegalStateException(exception);
    }
  }

  private static TeamMember memberByUserId(Display display, Long userId) {
    return display.getTeamMembers().stream()
        .filter(member -> member.getUserId().value().equals(userId))
        .findFirst()
        .orElseThrow();
  }

  private static Display display() {
    Display display =
        Display.create(
            new UserId(LEADER_USER_ID),
            "동시 대표자 위임 테스트",
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
        new TeamMember(null, new UserId(LEADER_USER_ID), "대표자", TeamMemberRole.TEAM_LEADER, true));
    display.addTeamMember(
        new TeamMember(
            null, new UserId(FIRST_TARGET_USER_ID), "첫 번째 팀원", TeamMemberRole.TEAM_MEM, true));
    display.addTeamMember(
        new TeamMember(
            null, new UserId(SECOND_TARGET_USER_ID), "두 번째 관리자", TeamMemberRole.MANAGER, true));
    return display;
  }

  private record TransferFixture(Long firstTargetMemberId, Long secondTargetMemberId) {}
}
