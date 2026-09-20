package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.repository.DisplayScreeningRepository;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayScreeningJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayScreeningRepositoryAdapter implements DisplayScreeningRepository {

  private final SpringDataDisplayScreeningJpaRepository jpaRepository;

  public JpaDisplayScreeningRepositoryAdapter(
      SpringDataDisplayScreeningJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<DisplayScreening> findLatestByDisplayId(Long displayId) {
    return jpaRepository.findFirstByDisplayIdOrderByIdDesc(displayId);
  }

  @Override
  public DisplayScreening save(DisplayScreening screening) {
    return jpaRepository.save(screening);
  }
}
