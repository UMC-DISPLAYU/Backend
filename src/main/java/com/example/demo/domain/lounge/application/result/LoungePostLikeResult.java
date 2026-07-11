package com.example.demo.domain.lounge.application.result;

public record LoungePostLikeResult(Long loungePostId, boolean liked, long likeCount) {}
