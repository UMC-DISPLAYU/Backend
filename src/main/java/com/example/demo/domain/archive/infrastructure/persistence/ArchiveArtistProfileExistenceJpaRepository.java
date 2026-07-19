package com.example.demo.domain.archive.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchiveArtistProfileExistenceJpaRepository
    extends JpaRepository<ArchiveArtistProfileReferenceJpaEntity, Long> {}
