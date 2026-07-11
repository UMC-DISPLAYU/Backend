package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.domain.entity.LoungePostLike;
import com.example.demo.domain.lounge.domain.repository.LoungePostLikeRepository;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungePostLikeJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLoungePostLikeRepositoryAdapter implements LoungePostLikeRepository {

  private final SpringDataLoungePostLikeJpaRepository jpaRepository;

  public JpaLoungePostLikeRepositoryAdapter(SpringDataLoungePostLikeJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<LoungePostLike> findByLoungePostIdAndUserId(Long loungePostId, UserId userId) {
    return jpaRepository.findByLoungePostIdAndUserId(loungePostId, userId);
  }

  @Override
  public LoungePostLike save(LoungePostLike loungePostLike) {
    return jpaRepository.save(loungePostLike);
  }

  @Override
  public void delete(LoungePostLike loungePostLike) {
    jpaRepository.delete(loungePostLike);
  }

  @Override
  public long countByLoungePostId(Long loungePostId) {
    return jpaRepository.countByLoungePostId(loungePostId);
  }
}
