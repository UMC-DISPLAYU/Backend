package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import java.util.List;
import java.util.Optional;

public interface ArtworkFeelingReplyRepository {
  ArtworkFeelingReply save(ArtworkFeelingReply artworkFeelingReply);

  Optional<ArtworkFeelingReply> findById(Long feelingReplyId);

  List<ArtworkFeelingReply> findActiveByFeelingIdWithCursor(
      Long feelingId, Long cursorId, int limit);

  java.util.Map<Long, Long> countActiveByFeelingIds(List<Long> feelingIds);
}
