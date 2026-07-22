package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;

public interface PersonalArtworkQuestionReplyRepository {
  PersonalArtworkQuestionReply save(PersonalArtworkQuestionReply personalArtworkQuestionReply);
}
