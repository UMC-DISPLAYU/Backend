package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import java.util.List;
import java.util.Optional;

public interface PersonalArtworkFeelingReplyRepository {
  PersonalArtworkFeelingReply save(PersonalArtworkFeelingReply personalArtworkFeelingReply);

  Optional<PersonalArtworkFeelingReply> findById(Long personalFeelingReplyId);

  List<PersonalArtworkFeelingReply> findActiveByPersonalFeelingIds(List<Long> personalFeelingIds);
}
