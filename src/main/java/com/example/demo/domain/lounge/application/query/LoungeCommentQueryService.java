package com.example.demo.domain.lounge.application.query;

import com.example.demo.domain.lounge.application.result.LoungeCommentCursorResult;
import com.example.demo.domain.lounge.application.result.LoungeCommentListResult;
import com.example.demo.domain.lounge.application.result.LoungeReplyCursorResult;
import com.example.demo.domain.lounge.application.result.WriterView;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeWriterRepository;
import com.example.demo.domain.lounge.domain.vo.LoungeWriter;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoungeCommentQueryService {
  private static final int MAX_PAGE_SIZE = 50;

  private final LoungePostRepository loungePostRepository;
  private final LoungeCommentRepository loungeCommentRepository;
  private final LoungeCommentLikeRepository loungeCommentLikeRepository;
  private final LoungeWriterRepository loungeWriterRepository;

  public LoungeCommentQueryService(
      LoungePostRepository loungePostRepository,
      LoungeCommentRepository loungeCommentRepository,
      LoungeCommentLikeRepository loungeCommentLikeRepository,
      LoungeWriterRepository loungeWriterRepository) {
    this.loungePostRepository = loungePostRepository;
    this.loungeCommentRepository = loungeCommentRepository;
    this.loungeCommentLikeRepository = loungeCommentLikeRepository;
    this.loungeWriterRepository = loungeWriterRepository;
  }

  @Transactional(readOnly = true)
  public LoungeCommentCursorResult getComments(
      Long loungePostId, Long cursorId, int size, Long viewerUserId) {
    LoungePost loungePost = getActivePost(loungePostId);
    int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    List<LoungeComment> fetched =
        loungeCommentRepository.findActiveRootByCursor(loungePost.getId(), cursorId, pageSize + 1);
    boolean hasNext = fetched.size() > pageSize;
    List<LoungeComment> comments = hasNext ? fetched.subList(0, pageSize) : fetched;
    if (comments.isEmpty()) {
      return new LoungeCommentCursorResult(List.of(), null, pageSize, false);
    }

    List<LoungeCommentListResult> results = toResults(comments, viewerUserId, true);
    Long nextCursorId = hasNext ? results.get(results.size() - 1).loungeCommentId() : null;
    return new LoungeCommentCursorResult(results, nextCursorId, pageSize, hasNext);
  }

  @Transactional(readOnly = true)
  public LoungeReplyCursorResult getReplies(
      Long parentCommentId, Long cursorId, int size, Long viewerUserId) {
    LoungeComment parentComment = getActiveComment(parentCommentId);
    if (!parentComment.isRootComment()) {
      throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
    }

    int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    List<LoungeComment> fetched =
        loungeCommentRepository.findActiveRepliesByCursor(
            parentComment.getId(), cursorId, pageSize + 1);
    boolean hasNext = fetched.size() > pageSize;
    List<LoungeComment> replies = hasNext ? fetched.subList(0, pageSize) : fetched;
    if (replies.isEmpty()) {
      return new LoungeReplyCursorResult(List.of(), null, pageSize, false);
    }

    List<LoungeCommentListResult> results = toResults(replies, viewerUserId, false);
    Long nextCursorId = hasNext ? results.get(results.size() - 1).loungeCommentId() : null;
    return new LoungeReplyCursorResult(results, nextCursorId, pageSize, hasNext);
  }

  private List<LoungeCommentListResult> toResults(
      List<LoungeComment> comments, Long viewerUserId, boolean includeReplyCount) {
    List<Long> commentIds = comments.stream().map(LoungeComment::getId).toList();
    Map<Long, Long> likeCounts = loungeCommentLikeRepository.countByLoungeCommentIds(commentIds);
    Map<Long, Long> replyCounts =
        includeReplyCount
            ? loungeCommentRepository.countActiveRepliesByParentCommentIds(commentIds)
            : Map.of();
    Set<Long> likedCommentIds =
        loungeCommentLikeRepository.findLikedLoungeCommentIds(commentIds, new UserId(viewerUserId));
    Map<Long, LoungeWriter> writers =
        loungeWriterRepository.findByUserIds(
            comments.stream()
                .map(comment -> comment.getAuthorUserId().value())
                .distinct()
                .toList());

    return comments.stream()
        .map(
            comment ->
                LoungeCommentListResult.from(
                    comment,
                    toWriterView(
                        writers.getOrDefault(
                            comment.getAuthorUserId().value(),
                            LoungeWriter.unknown(comment.getAuthorUserId().value()))),
                    likeCounts.getOrDefault(comment.getId(), 0L),
                    replyCounts.getOrDefault(comment.getId(), 0L),
                    likedCommentIds.contains(comment.getId()),
                    viewerUserId))
        .toList();
  }

  private WriterView toWriterView(LoungeWriter writer) {
    return new WriterView(writer.userId(), writer.nickname(), writer.profileImageUrl());
  }

  private LoungePost getActivePost(Long loungePostId) {
    return loungePostRepository
        .findById(loungePostId)
        .filter(post -> !post.isDeleted())
        .filter(LoungePost::isActive)
        .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
  }

  private LoungeComment getActiveComment(Long loungeCommentId) {
    return loungeCommentRepository
        .findById(loungeCommentId)
        .filter(comment -> !comment.isDeleted())
        .filter(LoungeComment::isActive)
        .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
  }
}
