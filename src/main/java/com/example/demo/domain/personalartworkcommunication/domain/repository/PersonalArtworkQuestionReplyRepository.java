package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import java.util.List;
import java.util.Optional;

public interface PersonalArtworkQuestionReplyRepository {
  PersonalArtworkQuestionReply save(PersonalArtworkQuestionReply personalArtworkQuestionReply);

  List<PersonalArtworkQuestionReply> findActiveByPersonalQuestionIds(
      List<Long> personalQuestionIds);

  Optional<PersonalArtworkQuestionReply> findActiveByIdForUpdate(Long personalQuestionReplyId);
}
