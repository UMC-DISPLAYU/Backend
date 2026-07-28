package com.example.demo.domain.display.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDuPickQueryJpaRepository
    extends JpaRepository<DuPickColumnJpaEntity, Long> {}
