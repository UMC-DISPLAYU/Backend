package com.example.demo.domain.lounge.presentation.response;

public record LoungePostLikeResponse(Long loungePostId, boolean liked, long likeCount) {}
