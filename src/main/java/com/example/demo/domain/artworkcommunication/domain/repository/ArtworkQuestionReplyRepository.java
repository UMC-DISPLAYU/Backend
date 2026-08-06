package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import java.util.List;
import java.util.Optional;

public interface ArtworkQuestionReplyRepository {
  ArtworkQuestionReply save(ArtworkQuestionReply artworkQuestionReply);

  List<ArtworkQuestionReply> findActiveByQuestionIds(List<Long> questionIds);

  Optional<ArtworkQuestionReply> findActiveByIdForUpdate(Long questionReplyId);
}
