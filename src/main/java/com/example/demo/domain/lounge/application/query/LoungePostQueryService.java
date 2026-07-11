package com.example.demo.domain.lounge.application.query;

import com.example.demo.domain.lounge.application.result.LoungePostCursorResult;
import com.example.demo.domain.lounge.application.result.LoungePostDetailResult;
import com.example.demo.domain.lounge.application.result.LoungePostListResult;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostScrapRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeWriterRepository;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
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
public class LoungePostQueryService {
  private static final int MAX_PAGE_SIZE = 50;

  private final LoungePostRepository loungePostRepository;
  private final LoungePostLikeRepository loungePostLikeRepository;
  private final LoungePostScrapRepository loungePostScrapRepository;
  private final LoungeCommentRepository loungeCommentRepository;
  private final LoungeWriterRepository loungeWriterRepository;

  public LoungePostQueryService(
      LoungePostRepository loungePostRepository,
      LoungePostLikeRepository loungePostLikeRepository,
      LoungePostScrapRepository loungePostScrapRepository,
      LoungeCommentRepository loungeCommentRepository,
      LoungeWriterRepository loungeWriterRepository) {
    this.loungePostRepository = loungePostRepository;
    this.loungePostLikeRepository = loungePostLikeRepository;
    this.loungePostScrapRepository = loungePostScrapRepository;
    this.loungeCommentRepository = loungeCommentRepository;
    this.loungeWriterRepository = loungeWriterRepository;
  }

  @Transactional(readOnly = true)
  public LoungePostCursorResult getPosts(
      LoungePostCategory category, Long cursorId, int size, Long viewerUserId) {
    int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    List<LoungePost> fetched =
        loungePostRepository.findActiveByCursor(category, cursorId, pageSize + 1);
    boolean hasNext = fetched.size() > pageSize;
    List<LoungePost> loungePosts = hasNext ? fetched.subList(0, pageSize) : fetched;
    if (loungePosts.isEmpty()) {
      return new LoungePostCursorResult(List.of(), null, pageSize, false);
    }

    List<Long> loungePostIds = loungePosts.stream().map(LoungePost::getId).toList();
    Map<Long, Long> likeCounts = loungePostLikeRepository.countByLoungePostIds(loungePostIds);
    Map<Long, Long> commentCounts =
        loungeCommentRepository.countActiveByLoungePostIds(loungePostIds);
    Set<Long> likedPostIds =
        loungePostLikeRepository.findLikedLoungePostIds(loungePostIds, new UserId(viewerUserId));
    Map<Long, LoungeWriter> writers =
        loungeWriterRepository.findByUserIds(
            loungePosts.stream().map(post -> post.getAuthorUserId().value()).distinct().toList());

    List<LoungePostListResult> posts =
        loungePosts.stream()
            .map(
                loungePost ->
                    LoungePostListResult.from(
                        loungePost,
                        writers.getOrDefault(
                            loungePost.getAuthorUserId().value(),
                            LoungeWriter.unknown(loungePost.getAuthorUserId().value())),
                        likeCounts.getOrDefault(loungePost.getId(), 0L),
                        commentCounts.getOrDefault(loungePost.getId(), 0L),
                        likedPostIds.contains(loungePost.getId()),
                        viewerUserId))
            .toList();
    Long nextCursorId = hasNext ? posts.get(posts.size() - 1).loungePostId() : null;
    return new LoungePostCursorResult(posts, nextCursorId, pageSize, hasNext);
  }

  @Transactional(readOnly = true)
  public LoungePostDetailResult getPostDetail(Long loungePostId) {
    return getPostDetail(loungePostId, null);
  }

  @Transactional(readOnly = true)
  public LoungePostDetailResult getPostDetail(Long loungePostId, Long viewerUserId) {
    LoungePost loungePost =
        loungePostRepository
            .findById(loungePostId)
            .filter(post -> !post.isDeleted())
            .filter(LoungePost::isActive)
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    Long resolvedViewerUserId =
        viewerUserId == null ? loungePost.getAuthorUserId().value() : viewerUserId;
    List<Long> loungePostIds = List.of(loungePost.getId());
    LoungeWriter writer =
        loungeWriterRepository
            .findByUserIds(List.of(loungePost.getAuthorUserId().value()))
            .getOrDefault(
                loungePost.getAuthorUserId().value(),
                LoungeWriter.unknown(loungePost.getAuthorUserId().value()));

    return LoungePostDetailResult.from(
        loungePost,
        writer,
        loungePostLikeRepository.countByLoungePostId(loungePost.getId()),
        loungeCommentRepository.countActiveByLoungePostId(loungePost.getId()),
        loungePostLikeRepository
            .findLikedLoungePostIds(loungePostIds, new UserId(resolvedViewerUserId))
            .contains(loungePost.getId()),
        loungePostScrapRepository
            .findScrappedLoungePostIds(loungePostIds, new UserId(resolvedViewerUserId))
            .contains(loungePost.getId()),
        resolvedViewerUserId);
  }
}
