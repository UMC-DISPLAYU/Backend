package com.example.demo.domain.artist.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistUserQualificationJpaRepository
    extends JpaRepository<ArtistUserQualificationJpaEntity, Long> {}
