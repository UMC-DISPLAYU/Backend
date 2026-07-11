package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungePostJpaRepository;
import java.util.List;
import java.util.Optional;
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
  public List<LoungePost> findAll() {
    return jpaRepository.findAll();
  }

  @Override
  public LoungePost save(LoungePost loungePost) {
    return jpaRepository.save(loungePost);
  }

  @Override
  public void delete(LoungePost loungePost) {
    jpaRepository.delete(loungePost);
  }
}
