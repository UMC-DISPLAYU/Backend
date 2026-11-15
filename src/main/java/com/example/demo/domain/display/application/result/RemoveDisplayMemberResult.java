package com.example.demo.domain.display.application.result;

public record RemoveDisplayMemberResult(
    Long displayId, Long removedMemberId, Long removedUserId, int deletedArtworkCount) {}
