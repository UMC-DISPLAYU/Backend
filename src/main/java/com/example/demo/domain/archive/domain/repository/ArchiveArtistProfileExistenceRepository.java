package com.example.demo.domain.archive.domain.repository;

public interface ArchiveArtistProfileExistenceRepository {
  boolean existsById(Long artistProfileId);
}
