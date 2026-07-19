package com.example.demo.domain.displayartwork.domain.repository;

public interface ArtistVerificationRepository {

  boolean isVerifiedArtist(Long userId);
}
