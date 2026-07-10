package com.example.demo.domain.lounge.application.command;

import com.example.demo.domain.lounge.application.result.LoungePostLikeResult;
import com.example.demo.domain.lounge.application.result.LoungePostScrapResult;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungePostLike;
import com.example.demo.domain.lounge.domain.entity.LoungePostScrap;
import com.example.demo.domain.lounge.domain.repository.LoungePostLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostScrapRepository;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoungePostCommandService {
    private final LoungePostRepository loungePostRepository;
    private final LoungePostLikeRepository loungePostLikeRepository;
    private final LoungePostScrapRepository loungePostScrapRepository;

    public LoungePostCommandService(
            LoungePostRepository loungePostRepository,
            LoungePostLikeRepository loungePostLikeRepository,
            LoungePostScrapRepository loungePostScrapRepository) {
        this.loungePostRepository = loungePostRepository;
        this.loungePostLikeRepository = loungePostLikeRepository;
        this.loungePostScrapRepository = loungePostScrapRepository;
    }

    @Transactional
    public Long createPost(Long authorUserId, LoungePostContentCommand command) {
        Objects.requireNonNull(command, "command must not be null.");

        LoungePost loungePost = LoungePost.create(
                new UserId(authorUserId),
                command.title(),
                command.postImageUrl(),
                command.content(),
                command.category());

        LoungePost savedPost = loungePostRepository.save(loungePost);
        return savedPost.getId();
    }

    @Transactional
    public void updatePost(Long loungePostId, Long requesterUserId, LoungePostContentCommand command) {
        Objects.requireNonNull(command, "command must not be null.");

        LoungePost loungePost = getPost(loungePostId);
        validateAuthor(loungePost, new UserId(requesterUserId));

        loungePost.changeContent(command.title(), command.postImageUrl(), command.content());
        loungePost.changeCategory(command.category());
    }

    @Transactional
    public void deletePost(Long loungePostId, Long requesterUserId) {
        LoungePost loungePost = getPost(loungePostId);
        validateAuthor(loungePost, new UserId(requesterUserId));
        loungePost.delete();
    }

    @Transactional
    public LoungePostLikeResult likePost(Long loungePostId, Long userId) {
        LoungePost loungePost = getActivePost(loungePostId);
        UserId likeUserId = new UserId(userId);

        loungePostLikeRepository
                .findByLoungePostIdAndUserId(loungePost.getId(), likeUserId)
                .orElseGet(
                        () -> loungePostLikeRepository.save(
                                LoungePostLike.create(loungePost.getId(), likeUserId)));

        return new LoungePostLikeResult(
                loungePost.getId(),
                true,
                loungePostLikeRepository.countByLoungePostId(loungePost.getId()));
    }

    @Transactional
    public LoungePostLikeResult cancelLikePost(Long loungePostId, Long userId) {
        LoungePost loungePost = getActivePost(loungePostId);
        UserId likeUserId = new UserId(userId);

        loungePostLikeRepository
                .findByLoungePostIdAndUserId(loungePost.getId(), likeUserId)
                .ifPresent(loungePostLikeRepository::delete);

        return new LoungePostLikeResult(
                loungePost.getId(),
                false,
                loungePostLikeRepository.countByLoungePostId(loungePost.getId()));
    }

    @Transactional
    public LoungePostScrapResult scrapPost(Long loungePostId, Long userId) {
        LoungePost loungePost = getActivePost(loungePostId);
        UserId scrapUserId = new UserId(userId);

        loungePostScrapRepository
                .findByLoungePostIdAndUserId(loungePost.getId(), scrapUserId)
                .orElseGet(
                        () -> loungePostScrapRepository.save(
                                LoungePostScrap.create(loungePost.getId(), scrapUserId)));

        return new LoungePostScrapResult(
                loungePost.getId(),
                true,
                loungePostScrapRepository.countByLoungePostId(loungePost.getId()));
    }

    @Transactional
    public LoungePostScrapResult cancelScrapPost(Long loungePostId, Long userId) {
        LoungePost loungePost = getActivePost(loungePostId);
        UserId scrapUserId = new UserId(userId);

        loungePostScrapRepository
                .findByLoungePostIdAndUserId(loungePost.getId(), scrapUserId)
                .ifPresent(loungePostScrapRepository::delete);

        return new LoungePostScrapResult(
                loungePost.getId(),
                false,
                loungePostScrapRepository.countByLoungePostId(loungePost.getId()));
    }

    private LoungePost getPost(Long loungePostId) {
        return loungePostRepository.findById(loungePostId)
                .filter(loungePost -> !loungePost.isDeleted())
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    }

    private LoungePost getActivePost(Long loungePostId) {
        LoungePost loungePost = getPost(loungePostId);
        if (!loungePost.isActive()) {
            throw new BusinessException(GlobalErrorCode.NOT_FOUND);
        }
        return loungePost;
    }

    private void validateAuthor(LoungePost loungePost, UserId requesterUserId) {
        if (!loungePost.getAuthorUserId().equals(requesterUserId)) {
            throw new BusinessException(GlobalErrorCode.FORBIDDEN);
        }
    }
}
