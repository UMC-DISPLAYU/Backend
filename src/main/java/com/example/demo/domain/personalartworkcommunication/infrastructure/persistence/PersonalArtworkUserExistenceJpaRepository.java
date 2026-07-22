package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalArtworkUserExistenceJpaRepository
    extends JpaRepository<PersonalArtworkUserReferenceJpaEntity, Long> {}
