package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataClosingSoonDisplayQueryJpaRepository
    extends JpaRepository<Display, Long> {

  @Query(
      """
      select new com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult(
        d.id,
        d.title,
        image.imageUrl,
        d.period.startDate,
        d.period.endDate
      )
      from Display d
      left join d.images image
        on image.imageType = :mainImageType
        and image.deletedAt is null
        and image.sortOrder = (
          select min(mainImage.sortOrder)
          from DisplayImage mainImage
          where mainImage.display = d
            and mainImage.imageType = :mainImageType
            and mainImage.deletedAt is null
        )
      where d.status = com.example.demo.domain.display.domain.type.DisplayStatus.PUBLISHED
        and d.period.endDate >= :today
        and (
          :cursorEndedAt is null
          or d.period.endDate > :cursorEndedAt
          or (
            d.period.endDate = :cursorEndedAt
            and d.id > :cursorDisplayId
          )
        )
      order by d.period.endDate asc, d.id asc
      """)
  List<ClosingSoonDisplayQueryResult> findClosingSoonDisplays(
      @Param("today") LocalDate today,
      @Param("cursorEndedAt") LocalDate cursorEndedAt,
      @Param("cursorDisplayId") Long cursorDisplayId,
      @Param("mainImageType") DisplayImageType mainImageType,
      Pageable pageable);
}
