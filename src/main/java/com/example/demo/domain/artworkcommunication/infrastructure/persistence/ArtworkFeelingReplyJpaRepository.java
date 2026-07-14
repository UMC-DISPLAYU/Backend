package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkFeelingReplyJpaRepository
    extends JpaRepository<ArtworkFeelingReply, Long> {}
