package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.PersonalArtworkFeelingReplyJpaRepository;
import java.util.List;
import java.util.Optional;
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

  @Override
  public Optional<PersonalArtworkFeelingReply> findById(Long personalFeelingReplyId) {
    return personalArtworkFeelingReplyJpaRepository.findById(personalFeelingReplyId);
  }

  @Override
  public List<PersonalArtworkFeelingReply> findActiveByPersonalFeelingIds(
      List<Long> personalFeelingIds) {
    return personalArtworkFeelingReplyJpaRepository
        .findByPersonalFeelingIdInAndDeletedAtIsNullOrderByCreatedAtAsc(personalFeelingIds);
  }
}
