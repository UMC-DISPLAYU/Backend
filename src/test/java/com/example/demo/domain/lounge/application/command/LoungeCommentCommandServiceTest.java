package com.example.demo.domain.lounge.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.error.LoungeErrorCode;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.type.LoungeCommentStatus;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.type.LoungePostStatus;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class LoungeCommentCommandServiceTest {

  private final LoungePostRepository postRepository = mock(LoungePostRepository.class);
  private final LoungeCommentRepository commentRepository = mock(LoungeCommentRepository.class);
  private final LoungeCommentLikeRepository commentLikeRepository =
      mock(LoungeCommentLikeRepository.class);
  private final LoungeCommentCommandService service =
      new LoungeCommentCommandService(postRepository, commentRepository, commentLikeRepository);

  @Test
  void createsCommentWithImages() {
    LoungePost post =
        new LoungePost(
            1L,
            new UserId(1L),
            "게시글 제목",
            "게시글 내용",
            LoungePostCategory.DISPLAY_REVIEW,
            LoungePostStatus.ACTIVE);
    List<String> imageUrls = List.of("https://image/1", "https://image/2");
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.createComment(1L, 2L, new LoungeCommentContentCommand("댓글 내용", imageUrls));

    ArgumentCaptor<LoungeComment> captor = ArgumentCaptor.forClass(LoungeComment.class);
    verify(commentRepository).save(captor.capture());
    assertThat(captor.getValue().getImageUrls()).containsExactlyElementsOf(imageUrls);
  }

  @Test
  void createsReplyWithImages() {
    LoungePost post =
        new LoungePost(
            1L,
            new UserId(1L),
            "게시글 제목",
            "게시글 내용",
            LoungePostCategory.DISPLAY_REVIEW,
            LoungePostStatus.ACTIVE);
    LoungeComment parentComment =
        new LoungeComment(2L, 1L, null, new UserId(2L), "댓글 내용", LoungeCommentStatus.ACTIVE);
    List<String> imageUrls = List.of("https://image/1", "https://image/2");
    when(commentRepository.findById(2L)).thenReturn(Optional.of(parentComment));
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.createReply(2L, 3L, new LoungeCommentContentCommand("답글 내용", imageUrls));

    ArgumentCaptor<LoungeComment> captor = ArgumentCaptor.forClass(LoungeComment.class);
    verify(commentRepository).save(captor.capture());
    assertThat(captor.getValue().getImageUrls()).containsExactlyElementsOf(imageUrls);
  }

  @Test
  void commentActionsReturnNotFoundWhenPostIsDeleted() {
    LoungePost post =
        LoungePost.create(new UserId(1L), "게시글 제목", "게시글 내용", LoungePostCategory.DISPLAY_REVIEW);
    post.delete();
    LoungeComment comment = LoungeComment.createComment(1L, new UserId(2L), "댓글 내용");
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

    assertCommentActionsReturnPostNotFound();
  }

  @Test
  void commentActionsReturnNotFoundWhenPostIsHidden() {
    LoungePost post =
        LoungePost.create(new UserId(1L), "게시글 제목", "게시글 내용", LoungePostCategory.DISPLAY_REVIEW);
    post.hide();
    LoungeComment comment = LoungeComment.createComment(1L, new UserId(2L), "댓글 내용");
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

    assertCommentActionsReturnPostNotFound();
  }

  private void assertCommentActionsReturnPostNotFound() {
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
