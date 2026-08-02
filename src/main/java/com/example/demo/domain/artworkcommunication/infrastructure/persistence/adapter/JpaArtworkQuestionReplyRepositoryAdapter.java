package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkQuestionReplyJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkQuestionReplyRepositoryAdapter implements ArtworkQuestionReplyRepository {

  private final ArtworkQuestionReplyJpaRepository artworkQuestionReplyJpaRepository;

  @Override
  public ArtworkQuestionReply save(ArtworkQuestionReply artworkQuestionReply) {
    return artworkQuestionReplyJpaRepository.save(artworkQuestionReply);
  }

  @Override
  public List<ArtworkQuestionReply> findActiveByQuestionIds(List<Long> questionIds) {
    return artworkQuestionReplyJpaRepository.findActiveByQuestionIds(questionIds);
  }

  @Override
  public Optional<ArtworkQuestionReply> findActiveByIdForUpdate(Long questionReplyId) {
    return artworkQuestionReplyJpaRepository.findActiveByIdForUpdate(questionReplyId);
  }
}
