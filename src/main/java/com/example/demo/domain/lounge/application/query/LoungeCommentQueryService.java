package com.example.demo.domain.lounge.application.query;

import com.example.demo.domain.lounge.application.result.LoungeCommentListResult;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoungeCommentQueryService {

  private final LoungePostRepository loungePostRepository;
  private final LoungeCommentRepository loungeCommentRepository;
  private final LoungeCommentLikeRepository loungeCommentLikeRepository;

  public LoungeCommentQueryService(
      LoungePostRepository loungePostRepository,
      LoungeCommentRepository loungeCommentRepository,
      LoungeCommentLikeRepository loungeCommentLikeRepository) {
    this.loungePostRepository = loungePostRepository;
    this.loungeCommentRepository = loungeCommentRepository;
    this.loungeCommentLikeRepository = loungeCommentLikeRepository;
  }

  @Transactional(readOnly = true)
  public List<LoungeCommentListResult> getComments(Long loungePostId) {
    LoungePost loungePost = getActivePost(loungePostId);
    List<LoungeComment> comments =
        loungeCommentRepository.findByLoungePostId(loungePost.getId()).stream()
            .filter(comment -> !comment.isDeleted())
            .filter(LoungeComment::isActive)
            .toList();

    Map<Long, List<LoungeComment>> repliesByParentId =
        comments.stream()
            .filter(LoungeComment::isReply)
            .collect(Collectors.groupingBy(LoungeComment::getParentCommentId));

    return comments.stream()
        .filter(LoungeComment::isRootComment)
        .sorted(Comparator.comparing(LoungeComment::getCreatedAt))
        .map(
            comment ->
                LoungeCommentListResult.from(
                    comment,
                    countLikes(comment),
                    toReplyResults(repliesByParentId.getOrDefault(comment.getId(), List.of()))))
        .toList();
  }

  private List<LoungeCommentListResult> toReplyResults(List<LoungeComment> replies) {
    return replies.stream()
        .sorted(Comparator.comparing(LoungeComment::getCreatedAt))
        .map(reply -> LoungeCommentListResult.from(reply, countLikes(reply), List.of()))
        .toList();
  }

  private long countLikes(LoungeComment comment) {
    return loungeCommentLikeRepository.countByLoungeCommentId(comment.getId());
  }

  private LoungePost getActivePost(Long loungePostId) {
    return loungePostRepository
        .findById(loungePostId)
        .filter(post -> !post.isDeleted())
        .filter(LoungePost::isActive)
        .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
  }
}
