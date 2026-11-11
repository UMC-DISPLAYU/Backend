package com.example.demo.domain.display.presentation.response;

public record RemoveDisplayMemberResponse(
    Long displayId, Long removedMemberId, Long removedUserId, int deletedArtworkCount) {}
