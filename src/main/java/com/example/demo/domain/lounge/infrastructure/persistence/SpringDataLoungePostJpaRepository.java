package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLoungePostJpaRepository extends JpaRepository<LoungePost, Long> {}
