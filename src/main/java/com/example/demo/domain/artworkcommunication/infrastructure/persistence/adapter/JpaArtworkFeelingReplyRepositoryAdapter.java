package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingReplyJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
  public Optional<ArtworkFeelingReply> findActiveByIdForUpdate(Long feelingReplyId) {
    return artworkFeelingReplyJpaRepository.findActiveByIdForUpdate(feelingReplyId);
  }

  @Override
  public List<ArtworkFeelingReply> findActiveByFeelingIdWithCursor(
      Long feelingId, Long cursorId, int limit) {
    return artworkFeelingReplyJpaRepository.findActiveByFeelingIdWithCursor(
        feelingId, cursorId, PageRequest.of(0, limit));
  }

  @Override
  public Map<Long, Long> countActiveByFeelingIds(List<Long> feelingIds) {
    return artworkFeelingReplyJpaRepository.countActiveByFeelingIds(feelingIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }
}
