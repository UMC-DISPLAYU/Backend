package com.example.demo.domain.archive.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataArchiveArtistProfileExistenceJpaRepository
    extends JpaRepository<ArchiveArtistProfileReferenceJpaEntity, Long> {}
