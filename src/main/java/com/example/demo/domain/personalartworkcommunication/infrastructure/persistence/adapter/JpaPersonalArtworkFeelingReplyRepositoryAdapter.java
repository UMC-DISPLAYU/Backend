package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.PersonalArtworkFeelingReplyJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
  public List<PersonalArtworkFeelingReply> findActiveByPersonalFeelingIdWithCursor(
      Long personalFeelingId, Long cursorId, int limit) {
    return personalArtworkFeelingReplyJpaRepository.findActiveByPersonalFeelingIdWithCursor(
        personalFeelingId, cursorId, PageRequest.of(0, limit));
  }

  @Override
  public Map<Long, Long> countActiveByPersonalFeelingIds(List<Long> personalFeelingIds) {
    return personalArtworkFeelingReplyJpaRepository
        .countActiveByPersonalFeelingIds(personalFeelingIds)
        .stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }
}
