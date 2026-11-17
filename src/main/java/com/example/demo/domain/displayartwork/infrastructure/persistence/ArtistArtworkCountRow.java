package com.example.demo.domain.displayartwork.infrastructure.persistence;

/** 전시 안에서 작가 계정 한 명이 작가로 등록된 작품 수. */
public record ArtistArtworkCountRow(Long userId, Long artworkCount) {}
