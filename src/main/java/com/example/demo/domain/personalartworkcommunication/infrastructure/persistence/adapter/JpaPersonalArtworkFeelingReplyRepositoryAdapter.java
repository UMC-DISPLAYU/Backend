package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.PersonalArtworkFeelingReplyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkFeelingReplyRepositoryAdapter
    implements PersonalArtworkFeelingReplyRepository {
  private final PersonalArtworkFeelingReplyJpaRepository personalArtworkFeelingReplyJpaRepository;

  @Override
  public PersonalArtworkFeelingReply save(PersonalArtworkFeelingReply personalArtworkFeelingReply) {
    return personalArtworkFeelingReplyJpaRepository.save(personalArtworkFeelingReply);
  }
}
