package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalArtworkQuestionReplyJpaRepository
    extends JpaRepository<PersonalArtworkQuestionReply, Long> {}
