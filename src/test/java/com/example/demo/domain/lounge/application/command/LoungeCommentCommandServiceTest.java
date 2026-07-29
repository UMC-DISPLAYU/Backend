package com.example.demo.domain.lounge.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.error.LoungeErrorCode;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LoungeCommentCommandServiceTest {

  private final LoungePostRepository postRepository = mock(LoungePostRepository.class);
  private final LoungeCommentRepository commentRepository = mock(LoungeCommentRepository.class);
  private final LoungeCommentLikeRepository commentLikeRepository =
      mock(LoungeCommentLikeRepository.class);
  private final LoungeCommentCommandService service =
      new LoungeCommentCommandService(postRepository, commentRepository, commentLikeRepository);

  @Test
  void commentChangesReturnNotFoundWhenPostIsDeleted() {
    LoungePost post =
        LoungePost.create(new UserId(1L), "게시글 제목", "게시글 내용", LoungePostCategory.DISPLAY_REVIEW);
    post.delete();
    LoungeComment comment = LoungeComment.createComment(1L, new UserId(2L), "댓글 내용");
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

    assertCommentChangesReturnPostNotFound();
  }

  @Test
  void commentChangesReturnNotFoundWhenPostIsHidden() {
    LoungePost post =
        LoungePost.create(new UserId(1L), "게시글 제목", "게시글 내용", LoungePostCategory.DISPLAY_REVIEW);
    post.hide();
    LoungeComment comment = LoungeComment.createComment(1L, new UserId(2L), "댓글 내용");
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

    assertCommentChangesReturnPostNotFound();
  }

  private void assertCommentChangesReturnPostNotFound() {
    assertPostNotFound(
        () -> service.updateComment(2L, 2L, new LoungeCommentContentCommand("수정 내용")));
    assertPostNotFound(() -> service.deleteComment(2L, 2L));
    assertPostNotFound(() -> service.likeComment(2L, 2L));
    assertPostNotFound(() -> service.cancelLikeComment(2L, 2L));
  }

  private static void assertPostNotFound(Runnable action) {
    assertThatThrownBy(action::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode()).isEqualTo(LoungeErrorCode.LOUNGE_POST_NOT_FOUND));
  }
}
