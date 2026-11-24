package com.example.demo.domain.report.infrastructure.persistence;

import com.example.demo.domain.report.domain.type.ReportTargetType;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataContentReportJpaRepository
    extends JpaRepository<ContentReportJpaEntity, Long> {
  List<ContentReportJpaEntity> findByTargetTypeAndTargetIdOrderByReportIdDesc(
      ReportTargetType targetType, Long targetId, Pageable pageable);

  List<ContentReportJpaEntity> findByTargetTypeAndTargetIdAndReportIdLessThanOrderByReportIdDesc(
      ReportTargetType targetType, Long targetId, Long cursor, Pageable pageable);
}
