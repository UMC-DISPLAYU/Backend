package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import java.util.List;

public interface PersonalArtworkQuestionReplyRepository {
  PersonalArtworkQuestionReply save(PersonalArtworkQuestionReply personalArtworkQuestionReply);

  List<PersonalArtworkQuestionReply> findActiveByPersonalQuestionIds(
      List<Long> personalQuestionIds);
}
