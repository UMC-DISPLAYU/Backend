package com.example.demo.domain.lounge.application.result;

public record LoungeCommentLikeResult(
        Long loungeCommentId,
        boolean liked,
        long likeCount) {}