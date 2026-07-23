package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import java.util.List;

public interface PersonalArtworkFeelingReplyRepository {
  PersonalArtworkFeelingReply save(PersonalArtworkFeelingReply personalArtworkFeelingReply);

  List<PersonalArtworkFeelingReply> findActiveByPersonalFeelingIds(List<Long> personalFeelingIds);
}
