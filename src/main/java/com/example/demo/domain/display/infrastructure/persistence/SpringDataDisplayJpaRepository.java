package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.domain.aggregate.Display;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDisplayJpaRepository extends JpaRepository<Display, Long> {}
