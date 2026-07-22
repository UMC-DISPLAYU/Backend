package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;

public interface PersonalArtworkFeelingReplyRepository {
  PersonalArtworkFeelingReply save(PersonalArtworkFeelingReply personalArtworkFeelingReply);
}
