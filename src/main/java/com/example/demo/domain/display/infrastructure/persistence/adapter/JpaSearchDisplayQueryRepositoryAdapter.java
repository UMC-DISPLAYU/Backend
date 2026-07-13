package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.SearchDisplayQuery;
import com.example.demo.domain.display.application.query.SearchDisplayQueryRepository;
import com.example.demo.domain.display.application.query.SearchDisplayQueryResult;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.SearchDisplayStatus;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataSearchDisplayQueryJpaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaSearchDisplayQueryRepositoryAdapter implements SearchDisplayQueryRepository {

  private final SpringDataSearchDisplayQueryJpaRepository jpaRepository;

  public JpaSearchDisplayQueryRepositoryAdapter(
      SpringDataSearchDisplayQueryJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<SearchDisplayQueryResult> searchDisplays(
      SearchDisplayQuery query, LocalDate today, int limit) {
    return jpaRepository.searchDisplays(
        query.searchWord(),
        query.status() != null,
        query.status() == SearchDisplayStatus.UPCOMING,
        query.status() == SearchDisplayStatus.ONGOING,
        query.status() == SearchDisplayStatus.ENDED,
        query.region() != null && query.region() != DisplayRegion.ALL,
        query.region(),
        query.field(),
        query.type(),
        today,
        query.cursor(),
        DisplayImageType.MAIN,
        PageRequest.of(0, limit));
  }
}
