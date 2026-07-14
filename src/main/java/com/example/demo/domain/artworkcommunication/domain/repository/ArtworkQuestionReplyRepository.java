package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import java.util.List;

public interface ArtworkQuestionReplyRepository {
  ArtworkQuestionReply save(ArtworkQuestionReply artworkQuestionReply);

  List<ArtworkQuestionReply> findActiveByQuestionIds(List<Long> questionIds);
}
