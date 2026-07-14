package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingReplyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkFeelingReplyRepositoryAdapter implements ArtworkFeelingReplyRepository {
  private final ArtworkFeelingReplyJpaRepository artworkFeelingReplyJpaRepository;

  @Override
  public ArtworkFeelingReply save(ArtworkFeelingReply artworkFeelingReply) {
    return artworkFeelingReplyJpaRepository.save(artworkFeelingReply);
  }
}
