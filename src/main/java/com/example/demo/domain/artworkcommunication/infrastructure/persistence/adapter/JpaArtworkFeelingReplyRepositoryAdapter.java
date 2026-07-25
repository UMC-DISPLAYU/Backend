package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingReplyJpaRepository;
import java.util.List;
import java.util.Optional;
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

  @Override
  public Optional<ArtworkFeelingReply> findById(Long feelingReplyId) {
    return artworkFeelingReplyJpaRepository.findById(feelingReplyId);
  }

  @Override
  public Optional<ArtworkFeelingReply> findActiveByFeelingId(Long feelingId) {
    return artworkFeelingReplyJpaRepository
        .findFirstByFeelingIdAndDeletedAtIsNullOrderByCreatedAtAsc(feelingId);
  }

  @Override
  public List<ArtworkFeelingReply> findActiveByFeelingIds(List<Long> feelingIds) {
    if (feelingIds.isEmpty()) {
      return List.of();
    }
    return artworkFeelingReplyJpaRepository.findByFeelingIdInAndDeletedAtIsNullOrderByCreatedAtAsc(
        feelingIds);
  }
}
