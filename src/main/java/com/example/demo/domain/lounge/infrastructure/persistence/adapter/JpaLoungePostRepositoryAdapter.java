package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.type.LoungePostStatus;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungePostJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLoungePostRepositoryAdapter implements LoungePostRepository {
  private final SpringDataLoungePostJpaRepository jpaRepository;

  public JpaLoungePostRepositoryAdapter(SpringDataLoungePostJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<LoungePost> findById(Long loungePostId) {
    return jpaRepository.findById(loungePostId);
  }

  @Override
  public List<LoungePost> findActiveByCursor(
      LoungePostCategory category, Long cursorId, int limit) {
    PageRequest pageRequest = PageRequest.of(0, limit);
    if (category == null) {
      return jpaRepository.findActiveByCursor(LoungePostStatus.ACTIVE, cursorId, pageRequest);
    }
    return jpaRepository.findActiveByCategoryAndCursor(
        LoungePostStatus.ACTIVE, category, cursorId, pageRequest);
  }

  @Override
  public List<LoungePost> findActiveByAuthorCursor(UserId authorUserId, Long cursorId, int limit) {
    return jpaRepository.findActiveByAuthorCursor(
        authorUserId.value(), LoungePostStatus.ACTIVE, cursorId, PageRequest.of(0, limit));
  }

  @Override
  public List<LoungePost> findAllByIds(List<Long> loungePostIds) {
    return jpaRepository.findAllById(loungePostIds);
  }

  @Override
  public LoungePost save(LoungePost loungePost) {
    return jpaRepository.save(loungePost);
  }
}
