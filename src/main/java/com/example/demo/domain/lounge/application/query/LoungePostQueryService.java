package com.example.demo.domain.lounge.application.query;

import com.example.demo.domain.lounge.application.result.LoungePostDetailResult;
import com.example.demo.domain.lounge.application.result.LoungePostListResult;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostScrapRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoungePostQueryService {

    private final LoungePostRepository loungePostRepository;
    private final LoungePostLikeRepository loungePostLikeRepository;
    private final LoungePostScrapRepository loungePostScrapRepository;
    private final LoungeCommentRepository loungeCommentRepository;

    public LoungePostQueryService(
            LoungePostRepository loungePostRepository,
            LoungePostLikeRepository loungePostLikeRepository,
            LoungePostScrapRepository loungePostScrapRepository,
            LoungeCommentRepository loungeCommentRepository) {
        this.loungePostRepository = loungePostRepository;
        this.loungePostLikeRepository = loungePostLikeRepository;
        this.loungePostScrapRepository = loungePostScrapRepository;
        this.loungeCommentRepository = loungeCommentRepository;
    }

    @Transactional(readOnly = true)
    public List<LoungePostListResult> getPosts() {
        return loungePostRepository.findAll().stream()
                .filter(loungePost -> !loungePost.isDeleted())
                .filter(LoungePost::isActive)
                .sorted(Comparator.comparing(LoungePost::getCreatedAt).reversed())
                .map(loungePost -> LoungePostListResult.from(
                        loungePost,
                        loungePostLikeRepository.countByLoungePostId(loungePost.getId()),
                        countActiveComments(loungePost.getId()),
                        loungePostScrapRepository.countByLoungePostId(loungePost.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public LoungePostDetailResult getPostDetail(Long loungePostId) {
        LoungePost loungePost = loungePostRepository.findById(loungePostId)
                .filter(post -> !post.isDeleted())
                .filter(LoungePost::isActive)
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

        return LoungePostDetailResult.from(
                loungePost,
                loungePostLikeRepository.countByLoungePostId(loungePost.getId()),
                countActiveComments(loungePost.getId()),
                loungePostScrapRepository.countByLoungePostId(loungePost.getId()));
    }

    private long countActiveComments(Long loungePostId) {
        return loungeCommentRepository.findByLoungePostId(loungePostId).stream()
                .filter(comment -> !comment.isDeleted())
                .filter(LoungeComment::isActive)
                .count();
    }
}