package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.application.query.DisplayMapQueryResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataDisplayMapQueryJpaRepository extends JpaRepository<Display, Long> {

  @Query(
      """
      select new com.example.demo.domain.display.application.query.DisplayMapQueryResult(
        d.id,
        d.title,
        d.period.startDate,
        d.period.endDate,
        d.location.placeName,
        image.imageUrl,
        d.location.latitude,
        d.location.longitude
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
        and d.location.latitude between :southLatitude and :northLatitude
        and d.location.longitude between :westLongitude and :eastLongitude
        and (:cursor is null or d.id < :cursor)
        and (
          :searchWord is null
          or :searchWord = ''
          or lower(d.title) like lower(concat('%', :searchWord, '%'))
          or lower(d.location.placeName) like lower(concat('%', :searchWord, '%'))
        )
      order by d.id desc
      """)
  List<DisplayMapQueryResult> findMarkers(
      @Param("southLatitude") BigDecimal southLatitude,
      @Param("westLongitude") BigDecimal westLongitude,
      @Param("northLatitude") BigDecimal northLatitude,
      @Param("eastLongitude") BigDecimal eastLongitude,
      @Param("searchWord") String searchWord,
      @Param("cursor") Long cursor,
      @Param("mainImageType") DisplayImageType mainImageType,
      Pageable pageable);
}
