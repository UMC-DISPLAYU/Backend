package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.domain.entity.DisplayContent;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayContentStatus;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataDisplayContentJpaRepository extends JpaRepository<DisplayContent, Long> {

  @Modifying(flushAutomatically = true)
  @Query(
      """
      UPDATE DisplayContent content
      SET content.status = :publishedStatus
      WHERE content.status = :draftStatus
        AND content.category.id IN (
          SELECT category.id
          FROM DisplayContentCategory category
          WHERE category.display.status = :displayStatus
            AND category.display.exhibitionContentOpen = :openPolicy
            AND category.display.period.startDate <= :today
        )
      """)
  int publishOnExhibition(
      @Param("today") LocalDate today,
      @Param("displayStatus") DisplayStatus displayStatus,
      @Param("openPolicy") ContentOpenPolicy openPolicy,
      @Param("draftStatus") DisplayContentStatus draftStatus,
      @Param("publishedStatus") DisplayContentStatus publishedStatus);

  @Modifying(flushAutomatically = true)
  @Query(
      """
      UPDATE DisplayContent content
      SET content.status = :publishedStatus
      WHERE content.status = :draftStatus
        AND content.category.id IN (
          SELECT category.id
          FROM DisplayContentCategory category
          WHERE category.display.id = :displayId
            AND (category.display.exhibitionContentOpen = :immediatePolicy
                 OR category.display.period.startDate <= :today)
        )
      """)
  int publishForDisplay(
      @Param("displayId") Long displayId,
      @Param("today") LocalDate today,
      @Param("immediatePolicy") ContentOpenPolicy immediatePolicy,
      @Param("draftStatus") DisplayContentStatus draftStatus,
      @Param("publishedStatus") DisplayContentStatus publishedStatus);
}
