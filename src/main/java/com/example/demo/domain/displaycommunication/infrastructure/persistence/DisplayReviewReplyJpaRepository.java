package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayReviewReplyJpaRepository extends JpaRepository<DisplayReviewReply, Long> {}
