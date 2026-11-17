package com.example.demo.domain.report.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataContentReportJpaRepository
    extends JpaRepository<ContentReportJpaEntity, Long> {}
