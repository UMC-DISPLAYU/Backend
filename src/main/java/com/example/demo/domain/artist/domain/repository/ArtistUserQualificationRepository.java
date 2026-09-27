package com.example.demo.domain.artist.domain.repository;

import com.example.demo.domain.artist.domain.model.ArtistUserQualification;
import java.util.Optional;

public interface ArtistUserQualificationRepository {

  Optional<ArtistUserQualification> findByUserId(Long userId);

  ArtistUserQualification save(ArtistUserQualification qualification);
}
