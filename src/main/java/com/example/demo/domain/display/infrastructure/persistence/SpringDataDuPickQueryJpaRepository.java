package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.application.query.DuPickQueryResult;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataDuPickQueryJpaRepository
    extends JpaRepository<DuPickColumnJpaEntity, Long> {

  @Query(
      """
      select new com.example.demo.domain.display.application.query.DuPickQueryResult(
        c.id,
        c.name,
        c.content,
        c.columnImageUrl,
        c.createdAt
      )
      from DuPickColumnJpaEntity c
      where (:cursor is null or c.id > :cursor)
      order by c.id asc
      """)
  List<DuPickQueryResult> findDuPicks(@Param("cursor") Long cursor, Pageable pageable);
}
