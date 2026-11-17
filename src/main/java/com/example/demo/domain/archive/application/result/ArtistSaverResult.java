package com.example.demo.domain.archive.application.result;

import java.time.LocalDateTime;

/** 사용자 한 명이 작가 한 명을 저장한 기록. */
public record ArtistSaverResult(
    Long userId, Long artistUserId, Long artistProfileId, LocalDateTime savedAt) {}
