package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkQuestionReplyJpaRepository
    extends JpaRepository<ArtworkQuestionReply, Long> {}
