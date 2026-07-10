package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryRepository;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataClosingSoonDisplayQueryJpaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaClosingSoonDisplayQueryRepositoryAdapter
    implements ClosingSoonDisplayQueryRepository {

  private final SpringDataClosingSoonDisplayQueryJpaRepository jpaRepository;

  public JpaClosingSoonDisplayQueryRepositoryAdapter(
      SpringDataClosingSoonDisplayQueryJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<ClosingSoonDisplayQueryResult> findClosingSoonDisplays(LocalDate today) {
    return jpaRepository.findClosingSoonDisplays(today, DisplayImageType.MAIN);
  }
}
