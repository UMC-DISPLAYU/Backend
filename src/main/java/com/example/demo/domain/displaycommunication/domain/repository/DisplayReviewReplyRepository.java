package com.example.demo.domain.displaycommunication.domain.repository;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import java.util.Optional;

public interface DisplayReviewReplyRepository {
  DisplayReviewReply save(DisplayReviewReply displayReviewReply);

  Optional<DisplayReviewReply> findById(Long displayReviewReplyId);
}
