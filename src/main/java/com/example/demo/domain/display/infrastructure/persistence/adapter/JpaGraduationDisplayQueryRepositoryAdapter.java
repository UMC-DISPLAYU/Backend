package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.application.query.GraduationDisplayQueryRepository;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataGraduationDisplayQueryJpaRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaGraduationDisplayQueryRepositoryAdapter
    implements GraduationDisplayQueryRepository {

  private final SpringDataGraduationDisplayQueryJpaRepository jpaRepository;

  public JpaGraduationDisplayQueryRepositoryAdapter(
      SpringDataGraduationDisplayQueryJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<ClosingSoonDisplayQueryResult> findRandomGraduationDisplays(int size) {
    return jpaRepository.findRandomGraduationDisplays(
        DisplayImageType.MAIN, PageRequest.of(0, size));
  }
}
