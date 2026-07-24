package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayExistenceJpaRepository
    extends JpaRepository<DisplayReferenceJpaEntity, Long> {}
