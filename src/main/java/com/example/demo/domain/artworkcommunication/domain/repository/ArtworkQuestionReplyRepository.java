package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;

public interface ArtworkQuestionReplyRepository {
  ArtworkQuestionReply save(ArtworkQuestionReply artworkQuestionReply);
}
