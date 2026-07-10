package com.example.demo.domain.lounge.application.result;

import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import java.time.LocalDateTime;
import java.util.List;

public record LoungeCommentListResult(
        Long loungeCommentId,
        Long parentCommentId,
        Long authorUserId,
        String content,
        long likeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<LoungeCommentListResult> replies) {

    public static LoungeCommentListResult from(
            LoungeComment comment, long likeCount, List<LoungeCommentListResult> replies) {
        return new LoungeCommentListResult(
                comment.getId(),
                comment.getParentCommentId(),
                comment.getAuthorUserId().value(),
                comment.getContent(),
                likeCount,
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                replies);
    }
}
