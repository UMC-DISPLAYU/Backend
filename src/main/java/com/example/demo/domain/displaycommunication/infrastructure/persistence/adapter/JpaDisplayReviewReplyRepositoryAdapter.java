package com.example.demo.domain.displaycommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.DisplayReviewReplyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDisplayReviewReplyRepositoryAdapter implements DisplayReviewReplyRepository {
  private final DisplayReviewReplyJpaRepository repository;

  @Override
  public DisplayReviewReply save(DisplayReviewReply displayReviewReply) {
    return repository.save(displayReviewReply);
  }
}
