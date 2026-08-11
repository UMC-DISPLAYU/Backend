package com.example.demo.domain.lounge.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.lounge.application.permission.LoungePermissionChecker;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.error.LoungeErrorCode;
import com.example.demo.domain.lounge.domain.repository.LoungePostLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostScrapRepository;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.type.LoungePostStatus;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

class LoungePostCommandServiceTest {

  private final LoungePostRepository postRepository = mock(LoungePostRepository.class);
  private final LoungePostLikeRepository postLikeRepository = mock(LoungePostLikeRepository.class);
  private final LoungePostScrapRepository postScrapRepository =
      mock(LoungePostScrapRepository.class);
  private final LoungePermissionChecker permissionChecker = mock(LoungePermissionChecker.class);
  private final LoungePostCommandService service =
      new LoungePostCommandService(
          postRepository, postLikeRepository, postScrapRepository, permissionChecker);

  @Test
  void updatePostUsesOptimisticLockAndSavesChanges() {
    LoungePost post = activePost();
    when(postRepository.findByIdWithOptimisticLock(1L)).thenReturn(Optional.of(post));

    service.updatePost(
        1L,
        1L,
        new LoungePostContentCommand(
            "수정 제목", List.of("image-2", "image-1"), "수정 내용", LoungePostCategory.WORK_TIP));

    assertThat(post.getTitle()).isEqualTo("수정 제목");
    assertThat(post.getContent()).isEqualTo("수정 내용");
    assertThat(post.getPostImageUrls()).containsExactly("image-2", "image-1");
    assertThat(post.getCategory()).isEqualTo(LoungePostCategory.WORK_TIP);
    verify(postRepository).save(post);
  }

  @Test
  void deletePostUsesOptimisticLockAndSavesChanges() {
    LoungePost post = activePost();
    when(postRepository.findByIdWithOptimisticLock(1L)).thenReturn(Optional.of(post));

    service.deletePost(1L, 1L);

    assertThat(post.isDeleted()).isTrue();
    assertThat(post.getStatus()).isEqualTo(LoungePostStatus.DELETED);
    verify(postRepository).save(post);
  }

  @Test
  void updatePostTranslatesOptimisticLockConflict() {
    LoungePost post = activePost();
    when(postRepository.findByIdWithOptimisticLock(1L)).thenReturn(Optional.of(post));
    doThrow(new ObjectOptimisticLockingFailureException(LoungePost.class, 1L))
        .when(postRepository)
        .save(post);

    assertConflict(
        () ->
            service.updatePost(
                1L,
                1L,
                new LoungePostContentCommand(
                    "수정 제목", List.of(), "수정 내용", LoungePostCategory.DISPLAY_REVIEW)));
  }

  @Test
  void deletePostTranslatesOptimisticLockConflict() {
    LoungePost post = activePost();
    when(postRepository.findByIdWithOptimisticLock(1L)).thenReturn(Optional.of(post));
    doThrow(new ObjectOptimisticLockingFailureException(LoungePost.class, 1L))
        .when(postRepository)
        .save(post);

    assertConflict(() -> service.deletePost(1L, 1L));
  }

  private static LoungePost activePost() {
    return new LoungePost(
        1L,
        new UserId(1L),
        "기존 제목",
        List.of("image-1"),
        "기존 내용",
        LoungePostCategory.DISPLAY_REVIEW,
        LoungePostStatus.ACTIVE);
  }

  private static void assertConflict(Runnable action) {
    assertThatThrownBy(action::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(LoungeErrorCode.LOUNGE_POST_CONCURRENT_WRITE_CONFLICT));
  }
}
