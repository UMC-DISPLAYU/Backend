package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.type.LoungePostStatus;
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
      List<LoungePostCategory> categories, Long cursorId, int limit) {
    return jpaRepository.findActiveByCategoriesAndCursor(
        LoungePostStatus.ACTIVE, categories, cursorId, PageRequest.of(0, limit));
  }

  @Override
  public LoungePost save(LoungePost loungePost) {
    return jpaRepository.save(loungePost);
  }
}
