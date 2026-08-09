package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.config.JpaAuditingConfig;
import jakarta.persistence.OptimisticLockException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@DataJpaTest
@ActiveProfiles("test")
@Import({JpaAuditingConfig.class, JpaLoungePostRepositoryAdapter.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class JpaLoungePostOptimisticLockTest {

  @Autowired private LoungePostRepository postRepository;
  @Autowired private PlatformTransactionManager transactionManager;

  @Test
  void concurrentUpdatesRejectTheStaleTransaction() throws Exception {
    Long postId = savePost(List.of("image-1"));

    Throwable conflict =
        runConcurrentChanges(
            postId,
            post -> post.changeContent("먼저 수정", "먼저 수정한 내용"),
            post -> post.changeContent("늦은 수정", "늦게 수정한 내용"));

    assertOptimisticLockConflict(conflict);
    assertThat(findPost(postId).getTitle()).isEqualTo("먼저 수정");
  }

  @Test
  void concurrentUpdateRejectsTheStaleDelete() throws Exception {
    Long postId = savePost(List.of("image-1"));

    Throwable conflict =
        runConcurrentChanges(
            postId, post -> post.changeContent("먼저 수정", "먼저 수정한 내용"), LoungePost::delete);

    assertOptimisticLockConflict(conflict);
    assertThat(findPost(postId).isDeleted()).isFalse();
  }

  @Test
  void concurrentImageReplacementsKeepTheWinningOrder() throws Exception {
    Long postId = savePost(List.of("original"));

    Throwable conflict =
        runConcurrentChanges(
            postId,
            post -> post.replaceImages(List.of("winner-2", "winner-1")),
            post -> post.replaceImages(List.of("loser-1", "loser-2")));

    assertOptimisticLockConflict(conflict);
    assertThat(findPostImageUrls(postId)).containsExactly("winner-2", "winner-1");
  }

  private Throwable runConcurrentChanges(
      Long postId, Consumer<LoungePost> winningChange, Consumer<LoungePost> staleChange)
      throws Exception {
    CountDownLatch bothLoaded = new CountDownLatch(2);
    CountDownLatch winnerCommitted = new CountDownLatch(1);
    ExecutorService executor = Executors.newFixedThreadPool(2);
    TransactionTemplate transaction = new TransactionTemplate(transactionManager);

    try {
      Future<?> winner =
          executor.submit(
              () -> {
                try {
                  transaction.executeWithoutResult(
                      status -> {
                        LoungePost post = findWithLock(postId);
                        winningChange.accept(post);
                        bothLoaded.countDown();
                        await(bothLoaded);
                        postRepository.flush();
                      });
                } finally {
                  winnerCommitted.countDown();
                }
              });
      Future<Throwable> stale =
          executor.submit(
              () -> {
                try {
                  transaction.executeWithoutResult(
                      status -> {
                        LoungePost post = findWithLock(postId);
                        staleChange.accept(post);
                        bothLoaded.countDown();
                        await(bothLoaded);
                        await(winnerCommitted);
                        postRepository.flush();
                      });
                  return null;
                } catch (Throwable exception) {
                  return exception;
                }
              });

      winner.get(10, TimeUnit.SECONDS);
      return stale.get(10, TimeUnit.SECONDS);
    } finally {
      executor.shutdownNow();
    }
  }

  private Long savePost(List<String> imageUrls) {
    return new TransactionTemplate(transactionManager)
        .execute(
            status -> {
              LoungePost post =
                  postRepository.save(
                      LoungePost.create(
                          new UserId(1L),
                          "기존 제목",
                          imageUrls,
                          "기존 내용",
                          LoungePostCategory.DISPLAY_REVIEW));
              postRepository.flush();
              return post.getId();
            });
  }

  private LoungePost findPost(Long postId) {
    return new TransactionTemplate(transactionManager)
        .execute(status -> postRepository.findById(postId).orElseThrow());
  }

  private List<String> findPostImageUrls(Long postId) {
    return new TransactionTemplate(transactionManager)
        .execute(status -> postRepository.findById(postId).orElseThrow().getPostImageUrls());
  }

  private LoungePost findWithLock(Long postId) {
    return postRepository.findByIdWithOptimisticLock(postId).orElseThrow();
  }

  private static void await(CountDownLatch latch) {
    try {
      if (!latch.await(10, TimeUnit.SECONDS)) {
        throw new AssertionError("concurrent test timed out");
      }
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      throw new AssertionError("concurrent test interrupted", exception);
    }
  }

  private static void assertOptimisticLockConflict(Throwable conflict) {
    assertThat(conflict).isNotNull();
    assertThat(hasOptimisticLockCause(conflict)).isTrue();
  }

  private static boolean hasOptimisticLockCause(Throwable exception) {
    Throwable current = exception;
    while (current != null) {
      if (current instanceof OptimisticLockingFailureException
          || current instanceof OptimisticLockException) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }
}
