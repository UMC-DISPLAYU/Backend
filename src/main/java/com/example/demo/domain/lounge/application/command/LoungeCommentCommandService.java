package com.example.demo.domain.lounge.application.command;

import com.example.demo.domain.lounge.application.permission.LoungePermissionChecker;
import com.example.demo.domain.lounge.application.result.LoungeCommentLikeResult;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.error.LoungeErrorCode;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.type.LoungeCommentStatus;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoungeCommentCommandService {

  private final LoungePostRepository loungePostRepository;
  private final LoungeCommentRepository loungeCommentRepository;
  private final LoungeCommentLikeRepository loungeCommentLikeRepository;
  private final LoungePermissionChecker permissionChecker;

  public LoungeCommentCommandService(
      LoungePostRepository loungePostRepository,
      LoungeCommentRepository loungeCommentRepository,
      LoungeCommentLikeRepository loungeCommentLikeRepository,
      LoungePermissionChecker permissionChecker) {
    this.loungePostRepository = loungePostRepository;
    this.loungeCommentRepository = loungeCommentRepository;
    this.loungeCommentLikeRepository = loungeCommentLikeRepository;
    this.permissionChecker = permissionChecker;
  }

  @Transactional
  public Long createComment(
      Long loungePostId, Long authorUserId, LoungeCommentContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    LoungePost loungePost = getActivePost(loungePostId);
    permissionChecker.requireCategoryAccess(loungePost.getCategory(), authorUserId);

    LoungeComment comment =
        LoungeComment.createComment(
            loungePost.getId(), new UserId(authorUserId), command.content(), command.imageUrls());

    LoungeComment savedComment = loungeCommentRepository.save(comment);
    return savedComment.getId();
  }

  @Transactional
  public Long createReply(
      Long parentCommentId, Long authorUserId, LoungeCommentContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    LoungeComment parentComment = getComment(parentCommentId);
    LoungePost loungePost = getActivePost(parentComment.getLoungePostId());
    permissionChecker.requireCategoryAccess(loungePost.getCategory(), authorUserId);

    if (!parentComment.isRootComment()) {
      throw new BusinessException(LoungeErrorCode.INVALID_REPLY_TARGET);
    }

    LoungeComment reply =
        LoungeComment.createReply(
            loungePost.getId(),
            parentComment.getId(),
            new UserId(authorUserId),
            command.content(),
            command.imageUrls());

    LoungeComment savedReply = loungeCommentRepository.save(reply);
    return savedReply.getId();
  }

  @Transactional
  public void deleteComment(Long loungeCommentId, Long requesterUserId) {
    LoungeComment comment = getActiveComment(loungeCommentId);
    getActivePost(comment.getLoungePostId());
    permissionChecker.requireCommentWriter(comment, requesterUserId);

    comment.delete();
  }

  @Transactional
  public LoungeCommentLikeResult likeComment(Long loungeCommentId, Long userId) {
    LoungeComment comment = getActiveComment(loungeCommentId);
    LoungePost loungePost = getActivePost(comment.getLoungePostId());
    permissionChecker.requireCategoryAccess(loungePost.getCategory(), userId);
    UserId likeUserId = new UserId(userId);

    loungeCommentLikeRepository.saveIfAbsent(comment.getId(), likeUserId);

    return new LoungeCommentLikeResult(
        comment.getId(), true, loungeCommentLikeRepository.countByLoungeCommentId(comment.getId()));
  }

  @Transactional
  public LoungeCommentLikeResult cancelLikeComment(Long loungeCommentId, Long userId) {
    LoungeComment comment = getActiveComment(loungeCommentId);
    LoungePost loungePost = getActivePost(comment.getLoungePostId());
    permissionChecker.requireCategoryAccess(loungePost.getCategory(), userId);
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

  private LoungeComment getComment(Long loungeCommentId) {
    return loungeCommentRepository
        .findById(loungeCommentId)
        .filter(
            comment ->
                (comment.isActive() && !comment.isDeleted())
                    || (comment.getStatus() == LoungeCommentStatus.DELETED && comment.isDeleted()))
        .orElseThrow(() -> new BusinessException(LoungeErrorCode.LOUNGE_COMMENT_NOT_FOUND));
  }
}
