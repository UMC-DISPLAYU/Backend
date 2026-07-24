package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDisplayReviewReplyRepositoryAdapter implements DisplayReviewReplyRepository {
  private final SpringDataDisplayReviewReplyJpaRepository repository;

  @Override
  public DisplayReviewReply save(DisplayReviewReply displayReviewReply) {
    return repository.save(displayReviewReply);
  }
}
