package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.application.query.SearchDisplayQueryResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataSearchDisplayQueryJpaRepository extends JpaRepository<Display, Long> {

  @Query(
      """
      select new com.example.demo.domain.display.application.query.SearchDisplayQueryResult(
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
        and d.id > :cursor
        and (:searchWord is null or :searchWord = '' or lower(d.title) like lower(concat('%', :searchWord, '%')))
        and (:filterByRegion = false or d.region = :region)
        and (:type is null or d.displayType = :type)
        and (
          :field is null
          or exists (
            select 1
            from DisplayFieldSelection fieldSelection
            where fieldSelection.display = d
              and fieldSelection.field = :field
          )
        )
        and (
          :hasStatus = false
          or (:isUpcoming = true and d.period.startDate > :today)
          or (:isOngoing = true
            and d.period.startDate <= :today
            and d.period.endDate >= :today)
          or (:isEnded = true and d.period.endDate < :today)
        )
      order by d.id asc
      """)
  List<SearchDisplayQueryResult> searchDisplays(
      @Param("searchWord") String searchWord,
      @Param("hasStatus") boolean hasStatus,
      @Param("isUpcoming") boolean isUpcoming,
      @Param("isOngoing") boolean isOngoing,
      @Param("isEnded") boolean isEnded,
      @Param("filterByRegion") boolean filterByRegion,
      @Param("region") DisplayRegion region,
      @Param("field") DisplayField field,
      @Param("type") DisplayType type,
      @Param("today") LocalDate today,
      @Param("cursor") Long cursor,
      @Param("mainImageType") DisplayImageType mainImageType,
      Pageable pageable);
}
