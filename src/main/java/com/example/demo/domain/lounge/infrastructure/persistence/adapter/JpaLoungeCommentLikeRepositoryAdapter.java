package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.domain.entity.LoungeCommentLike;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentLikeRepository;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungeCommentLikeJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLoungeCommentLikeRepositoryAdapter implements LoungeCommentLikeRepository {

  private final SpringDataLoungeCommentLikeJpaRepository jpaRepository;

  public JpaLoungeCommentLikeRepositoryAdapter(
      SpringDataLoungeCommentLikeJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<LoungeCommentLike> findByLoungeCommentIdAndUserId(
      Long loungeCommentId, UserId userId) {
    return jpaRepository.findByLoungeCommentIdAndUserId(loungeCommentId, userId);
  }

  @Override
  public LoungeCommentLike save(LoungeCommentLike loungeCommentLike) {
    return jpaRepository.save(loungeCommentLike);
  }

  @Override
  public void delete(LoungeCommentLike loungeCommentLike) {
    jpaRepository.delete(loungeCommentLike);
  }

  @Override
  public long countByLoungeCommentId(Long loungeCommentId) {
    return jpaRepository.countByLoungeCommentId(loungeCommentId);
  }
}
