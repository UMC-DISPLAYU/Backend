package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.DisplayMapQuery;
import com.example.demo.domain.display.application.query.DisplayMapQueryRepository;
import com.example.demo.domain.display.application.query.DisplayMapQueryResult;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayMapQueryJpaRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayMapQueryRepositoryAdapter implements DisplayMapQueryRepository {

  private final SpringDataDisplayMapQueryJpaRepository jpaRepository;

  public JpaDisplayMapQueryRepositoryAdapter(SpringDataDisplayMapQueryJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<DisplayMapQueryResult> findMarkers(DisplayMapQuery query, int limit) {
    return jpaRepository.findMarkers(
        query.southLatitude(),
        query.westLongitude(),
        query.northLatitude(),
        query.eastLongitude(),
        query.searchWord(),
        query.cursor(),
        DisplayImageType.MAIN,
        PageRequest.of(0, limit));
  }
}
