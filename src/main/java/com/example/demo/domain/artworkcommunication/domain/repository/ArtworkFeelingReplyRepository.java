package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;

public interface ArtworkFeelingReplyRepository {
  ArtworkFeelingReply save(ArtworkFeelingReply artworkFeelingReply);
}
