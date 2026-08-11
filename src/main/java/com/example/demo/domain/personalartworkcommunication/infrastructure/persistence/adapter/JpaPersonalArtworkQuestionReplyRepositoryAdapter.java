package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkQuestionReplyJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkQuestionReplyRepositoryAdapter
    implements PersonalArtworkQuestionReplyRepository {

  private final SpringDataPersonalArtworkQuestionReplyJpaRepository repository;

  @Override
  public PersonalArtworkQuestionReply save(
      PersonalArtworkQuestionReply personalArtworkQuestionReply) {
    return repository.save(personalArtworkQuestionReply);
  }

  @Override
  public List<PersonalArtworkQuestionReply> findActiveByPersonalQuestionIds(
      List<Long> personalQuestionIds) {
    return repository.findByPersonalQuestionIdInAndDeletedAtIsNullOrderByCreatedAtAsc(
        personalQuestionIds);
  }

  @Override
  public Optional<PersonalArtworkQuestionReply> findActiveByIdForUpdate(
      Long personalQuestionReplyId) {
    return repository.findActiveByIdForUpdate(personalQuestionReplyId);
  }
}
