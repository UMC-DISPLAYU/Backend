package com.example.demo.domain.displaycommunication.domain.repository;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DisplayReviewReplyRepository {
  DisplayReviewReply save(DisplayReviewReply displayReviewReply);

  Optional<DisplayReviewReply> findById(Long displayReviewReplyId);

  void softDeleteAllByDisplayReviewId(Long displayReviewId);

  List<DisplayReviewReply> findActiveByDisplayReviewIdWithCursor(
      Long displayReviewId, Long cursorId, int limit);

  Map<Long, Long> countActiveByDisplayReviewIds(List<Long> displayReviewIds);
}
