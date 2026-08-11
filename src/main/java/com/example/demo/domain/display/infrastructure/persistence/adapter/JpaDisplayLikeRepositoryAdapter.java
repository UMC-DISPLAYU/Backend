package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.domain.entity.DisplayLike;
import com.example.demo.domain.display.domain.repository.DisplayLikeRepository;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayLikeJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayLikeRepositoryAdapter implements DisplayLikeRepository {

  private final SpringDataDisplayLikeJpaRepository jpaRepository;

  public JpaDisplayLikeRepositoryAdapter(SpringDataDisplayLikeJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<DisplayLike> findByDisplayIdAndUserId(Long displayId, UserId userId) {
    return jpaRepository.findByDisplayIdAndUserIdValue(displayId, userId.value());
  }

  @Override
  public DisplayLike save(DisplayLike displayLike) {
    return jpaRepository.saveAndFlush(displayLike);
  }

  @Override
  public int deleteByDisplayIdAndUserId(Long displayId, UserId userId) {
    return jpaRepository.deleteByDisplayIdAndUserId(displayId, userId.value());
  }

  @Override
  public long countByDisplayId(Long displayId) {
    return jpaRepository.countByDisplayId(displayId);
  }
}
