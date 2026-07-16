package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import java.util.List;
import java.util.Optional;

public interface ArtworkFeelingReplyRepository {
  ArtworkFeelingReply save(ArtworkFeelingReply artworkFeelingReply);

  Optional<ArtworkFeelingReply> findActiveByFeelingId(Long feelingId);

  List<ArtworkFeelingReply> findActiveByFeelingIds(List<Long> feelingIds);
}
