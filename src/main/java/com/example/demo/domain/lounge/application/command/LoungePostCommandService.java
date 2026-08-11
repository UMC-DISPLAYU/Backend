package com.example.demo.domain.lounge.application.command;

import com.example.demo.domain.lounge.application.permission.LoungePermissionChecker;
import com.example.demo.domain.lounge.application.result.LoungePostLikeResult;
import com.example.demo.domain.lounge.application.result.LoungePostScrapResult;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.error.LoungeErrorCode;
import com.example.demo.domain.lounge.domain.repository.LoungePostLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostScrapRepository;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import jakarta.persistence.OptimisticLockException;
import java.util.Objects;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoungePostCommandService {
  private final LoungePostRepository loungePostRepository;
  private final LoungePostLikeRepository loungePostLikeRepository;
  private final LoungePostScrapRepository loungePostScrapRepository;
  private final LoungePermissionChecker permissionChecker;

  public LoungePostCommandService(
      LoungePostRepository loungePostRepository,
      LoungePostLikeRepository loungePostLikeRepository,
      LoungePostScrapRepository loungePostScrapRepository,
      LoungePermissionChecker permissionChecker) {
    this.loungePostRepository = loungePostRepository;
    this.loungePostLikeRepository = loungePostLikeRepository;
    this.loungePostScrapRepository = loungePostScrapRepository;
    this.permissionChecker = permissionChecker;
  }

  @Transactional
  public Long createPost(Long authorUserId, LoungePostContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    permissionChecker.requireCategoryAccess(command.category(), authorUserId);

    LoungePost loungePost =
        LoungePost.create(
            new UserId(authorUserId),
            command.title(),
            command.postImageUrls(),
            command.content(),
            command.category());

    LoungePost savedPost = loungePostRepository.save(loungePost);
    return savedPost.getId();
  }

  @Transactional
  public void updatePost(
      Long loungePostId, Long requesterUserId, LoungePostContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    try {
      LoungePost loungePost = getPostWithOptimisticLock(loungePostId);
      permissionChecker.requirePostWriter(loungePost, requesterUserId);
      permissionChecker.requireCategoryAccess(loungePost.getCategory(), requesterUserId);
      permissionChecker.requireCategoryAccess(command.category(), requesterUserId);

      loungePost.changeContent(command.title(), command.content());
      loungePost.replaceImages(command.postImageUrls());
      loungePost.changeCategory(command.category());
      loungePostRepository.save(loungePost);
    } catch (OptimisticLockingFailureException | OptimisticLockException e) {
      throw new BusinessException(LoungeErrorCode.LOUNGE_POST_CONCURRENT_WRITE_CONFLICT, e);
    }
  }

  @Transactional
  public void deletePost(Long loungePostId, Long requesterUserId) {
    try {
      LoungePost loungePost = getPostWithOptimisticLock(loungePostId);
      permissionChecker.requirePostWriter(loungePost, requesterUserId);
      loungePost.delete();
      loungePostRepository.save(loungePost);
    } catch (OptimisticLockingFailureException | OptimisticLockException e) {
      throw new BusinessException(LoungeErrorCode.LOUNGE_POST_CONCURRENT_WRITE_CONFLICT, e);
    }
  }

  @Transactional
  public LoungePostLikeResult likePost(Long loungePostId, Long userId) {
    LoungePost loungePost = getActivePost(loungePostId);
    permissionChecker.requireCategoryAccess(loungePost.getCategory(), userId);
    UserId likeUserId = new UserId(userId);

    loungePostLikeRepository.saveIfAbsent(loungePost.getId(), likeUserId);

    return new LoungePostLikeResult(
        loungePost.getId(), true, loungePostLikeRepository.countByLoungePostId(loungePost.getId()));
  }

  @Transactional
  public LoungePostLikeResult cancelLikePost(Long loungePostId, Long userId) {
    LoungePost loungePost = getActivePost(loungePostId);
    permissionChecker.requireCategoryAccess(loungePost.getCategory(), userId);
    UserId likeUserId = new UserId(userId);

    loungePostLikeRepository.deleteByLoungePostIdAndUserId(loungePost.getId(), likeUserId);

    return new LoungePostLikeResult(
        loungePost.getId(),
        false,
        loungePostLikeRepository.countByLoungePostId(loungePost.getId()));
  }

  @Transactional
  public LoungePostScrapResult scrapPost(Long loungePostId, Long userId) {
    LoungePost loungePost = getActivePost(loungePostId);
    permissionChecker.requireCategoryAccess(loungePost.getCategory(), userId);
    UserId scrapUserId = new UserId(userId);

    loungePostScrapRepository.saveIfAbsent(loungePost.getId(), scrapUserId);

    return new LoungePostScrapResult(
        loungePost.getId(),
        true,
        loungePostScrapRepository.countByLoungePostId(loungePost.getId()));
  }

  @Transactional
  public LoungePostScrapResult cancelScrapPost(Long loungePostId, Long userId) {
    LoungePost loungePost = getActivePost(loungePostId);
    permissionChecker.requireCategoryAccess(loungePost.getCategory(), userId);
    UserId scrapUserId = new UserId(userId);

    loungePostScrapRepository.deleteByLoungePostIdAndUserId(loungePost.getId(), scrapUserId);

    return new LoungePostScrapResult(
        loungePost.getId(),
        false,
        loungePostScrapRepository.countByLoungePostId(loungePost.getId()));
  }

  private LoungePost getPost(Long loungePostId) {
    return loungePostRepository
        .findById(loungePostId)
        .filter(loungePost -> !loungePost.isDeleted())
        .orElseThrow(() -> new BusinessException(LoungeErrorCode.LOUNGE_POST_NOT_FOUND));
  }

  private LoungePost getActivePost(Long loungePostId) {
    LoungePost loungePost = getPost(loungePostId);
    if (!loungePost.isActive()) {
      throw new BusinessException(LoungeErrorCode.LOUNGE_POST_NOT_FOUND);
    }
    return loungePost;
  }

  private LoungePost getPostWithOptimisticLock(Long loungePostId) {
    return loungePostRepository
        .findByIdWithOptimisticLock(loungePostId)
        .filter(loungePost -> !loungePost.isDeleted())
        .orElseThrow(() -> new BusinessException(LoungeErrorCode.LOUNGE_POST_NOT_FOUND));
  }
}
