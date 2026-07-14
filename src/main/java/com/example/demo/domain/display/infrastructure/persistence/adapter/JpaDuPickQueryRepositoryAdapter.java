package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.DuPickQuery;
import com.example.demo.domain.display.application.query.DuPickQueryRepository;
import com.example.demo.domain.display.application.query.DuPickQueryResult;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDuPickQueryJpaRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDuPickQueryRepositoryAdapter implements DuPickQueryRepository {

  private final SpringDataDuPickQueryJpaRepository jpaRepository;

  public JpaDuPickQueryRepositoryAdapter(SpringDataDuPickQueryJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<DuPickQueryResult> findDuPicks(DuPickQuery query, int limit) {
    return jpaRepository.findDuPicks(query.cursor(), PageRequest.of(0, limit));
  }
}
