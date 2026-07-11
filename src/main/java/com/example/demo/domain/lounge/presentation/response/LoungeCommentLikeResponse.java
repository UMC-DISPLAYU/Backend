package com.example.demo.domain.lounge.presentation.response;

public record LoungeCommentLikeResponse(Long loungeCommentId, boolean liked, long likeCount) {}
