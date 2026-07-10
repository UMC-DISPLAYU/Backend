package com.example.demo.domain.lounge.application.result;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;

import java.time.LocalDateTime;

public record LoungePostListResult(
        Long loungePostId,
        Long authorUserId,
        String title,
        String postImageUrl,
        String category,
        long likeCount,
        long commentCount,
        long scrapCount,
        LocalDateTime createdAt
) {
    public static LoungePostListResult from(
            LoungePost loungePost,
            long likeCount,
            long commentCount,
            long scrapCount) {
        return new LoungePostListResult(
                loungePost.getId(),
                loungePost.getAuthorUserId().value(),
                loungePost.getTitle(),
                loungePost.getPostImageUrl(),
                loungePost.getCategory().name(),
                likeCount,
                commentCount,
                scrapCount,
                loungePost.getCreatedAt());
}}
