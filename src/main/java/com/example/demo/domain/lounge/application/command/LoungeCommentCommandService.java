package com.example.demo.domain.lounge.application.command;

import com.example.demo.domain.lounge.application.result.LoungeCommentLikeResult;
import com.example.demo.domain.lounge.application.result.LoungeCommentListResult;
import com.example.demo.domain.lounge.application.result.WriterView;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.error.LoungeErrorCode;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeWriterRepository;
import com.example.demo.domain.lounge.domain.vo.LoungeWriter;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoungeCommentCommandService {

  private final LoungePostRepository loungePostRepository;
  private final LoungeCommentRepository loungeCommentRepository;
  private final LoungeCommentLikeRepository loungeCommentLikeRepository;
  private final LoungeWriterRepository loungeWriterRepository;

  public LoungeCommentCommandService(
      LoungePostRepository loungePostRepository,
      LoungeCommentRepository loungeCommentRepository,
      LoungeCommentLikeRepository loungeCommentLikeRepository,
      LoungeWriterRepository loungeWriterRepository) {
    this.loungePostRepository = loungePostRepository;
    this.loungeCommentRepository = loungeCommentRepository;
    this.loungeCommentLikeRepository = loungeCommentLikeRepository;
    this.loungeWriterRepository = loungeWriterRepository;
  }

  @Transactional
  public LoungeCommentListResult createComment(
      Long loungePostId, Long authorUserId, LoungeCommentContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    LoungePost loungePost = getActivePost(loungePostId);

    LoungeComment comment =
        LoungeComment.createComment(
            loungePost.getId(), new UserId(authorUserId), command.content());

    LoungeComment savedComment = loungeCommentRepository.save(comment);
    return LoungeCommentListResult.from(
        savedComment, getWriterView(authorUserId), 0, 0, false, authorUserId);
  }

  @Transactional
  public LoungeCommentListResult createReply(
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
    return LoungeCommentListResult.from(
        savedReply, getWriterView(authorUserId), 0, 0, false, authorUserId);
  }

  @Transactional
  public void updateComment(
      Long loungeCommentId, Long requesterUserId, LoungeCommentContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    LoungeComment comment = getActiveComment(loungeCommentId);
    validateAuthor(comment, new UserId(requesterUserId));

    comment.changeContent(command.content());
  }

  @Transactional
  public void deleteComment(Long loungeCommentId, Long requesterUserId) {
    LoungeComment comment = getActiveComment(loungeCommentId);
    validateAuthor(comment, new UserId(requesterUserId));

    comment.delete();
  }

  @Transactional
  public LoungeCommentLikeResult likeComment(Long loungeCommentId, Long userId) {
    LoungeComment comment = getActiveComment(loungeCommentId);
    UserId likeUserId = new UserId(userId);

    loungeCommentLikeRepository.saveIfAbsent(comment.getId(), likeUserId);

    return new LoungeCommentLikeResult(
        comment.getId(), true, loungeCommentLikeRepository.countByLoungeCommentId(comment.getId()));
  }

  @Transactional
  public LoungeCommentLikeResult cancelLikeComment(Long loungeCommentId, Long userId) {
    LoungeComment comment = getActiveComment(loungeCommentId);
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

  private WriterView getWriterView(Long userId) {
    LoungeWriter writer =
        loungeWriterRepository
            .findByUserIds(List.of(userId))
            .getOrDefault(userId, LoungeWriter.unknown(userId));

    return new WriterView(writer.userId(), writer.nickname(), writer.profileImageUrl());
  }
}
