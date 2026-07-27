package com.example.demo.domain.lounge.application.command;

import com.example.demo.domain.lounge.application.result.LoungeCommentLikeResult;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.error.LoungeErrorCode;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoungeCommentCommandService {

  private final LoungePostRepository loungePostRepository;
  private final LoungeCommentRepository loungeCommentRepository;
  private final LoungeCommentLikeRepository loungeCommentLikeRepository;

  public LoungeCommentCommandService(
      LoungePostRepository loungePostRepository,
      LoungeCommentRepository loungeCommentRepository,
      LoungeCommentLikeRepository loungeCommentLikeRepository) {
    this.loungePostRepository = loungePostRepository;
    this.loungeCommentRepository = loungeCommentRepository;
    this.loungeCommentLikeRepository = loungeCommentLikeRepository;
  }

  @Transactional
  public Long createComment(
      Long loungePostId, Long authorUserId, LoungeCommentContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    LoungePost loungePost = getActivePost(loungePostId);

    LoungeComment comment =
        LoungeComment.createComment(
            loungePost.getId(), new UserId(authorUserId), command.content());

    LoungeComment savedComment = loungeCommentRepository.save(comment);
    return savedComment.getId();
  }

  @Transactional
  public Long createReply(
      Long parentCommentId, Long authorUserId, LoungeCommentContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    LoungeComment parentComment = getActiveComment(parentCommentId);
    LoungePost loungePost = getActivePost(parentComment.getLoungePostId());

    if (!parentComment.isRootComment()) {
      throw new BusinessException(LoungeErrorCode.INVALID_REPLY_TARGET);
    }

    LoungeComment reply =
        LoungeComment.createReply(
            loungePost.getId(), parentComment.getId(), new UserId(authorUserId), command.content());

    LoungeComment savedReply = loungeCommentRepository.save(reply);
    return savedReply.getId();
  }

  @Transactional
  public void updateComment(
      Long loungeCommentId, Long requesterUserId, LoungeCommentContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    LoungeComment comment = getActiveComment(loungeCommentId);
    getActivePost(comment.getLoungePostId());
    validateAuthor(comment, new UserId(requesterUserId));

    comment.changeContent(command.content());
  }

  @Transactional
  public void deleteComment(Long loungeCommentId, Long requesterUserId) {
    LoungeComment comment = getActiveComment(loungeCommentId);
    getActivePost(comment.getLoungePostId());
    validateAuthor(comment, new UserId(requesterUserId));

    comment.delete();
  }

  @Transactional
  public LoungeCommentLikeResult likeComment(Long loungeCommentId, Long userId) {
    LoungeComment comment = getActiveComment(loungeCommentId);
    getActivePost(comment.getLoungePostId());
    UserId likeUserId = new UserId(userId);

    loungeCommentLikeRepository.saveIfAbsent(comment.getId(), likeUserId);

    return new LoungeCommentLikeResult(
        comment.getId(), true, loungeCommentLikeRepository.countByLoungeCommentId(comment.getId()));
  }

  @Transactional
  public LoungeCommentLikeResult cancelLikeComment(Long loungeCommentId, Long userId) {
    LoungeComment comment = getActiveComment(loungeCommentId);
    getActivePost(comment.getLoungePostId());
    UserId likeUserId = new UserId(userId);

    loungeCommentLikeRepository.deleteByLoungeCommentIdAndUserId(comment.getId(), likeUserId);

    return new LoungeCommentLikeResult(
        comment.getId(),
        false,
        loungeCommentLikeRepository.countByLoungeCommentId(comment.getId()));
  }

  private LoungePost getActivePost(Long loungePostId) {
    return loungePostRepository
        .findById(loungePostId)
        .filter(post -> !post.isDeleted())
        .filter(LoungePost::isActive)
        .orElseThrow(() -> new BusinessException(LoungeErrorCode.LOUNGE_POST_NOT_FOUND));
  }

  private LoungeComment getActiveComment(Long loungeCommentId) {
    return loungeCommentRepository
        .findById(loungeCommentId)
        .filter(comment -> !comment.isDeleted())
        .filter(LoungeComment::isActive)
        .orElseThrow(() -> new BusinessException(LoungeErrorCode.LOUNGE_COMMENT_NOT_FOUND));
  }

  private void validateAuthor(LoungeComment comment, UserId requesterUserId) {
    if (!comment.getAuthorUserId().equals(requesterUserId)) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }
  }
}
