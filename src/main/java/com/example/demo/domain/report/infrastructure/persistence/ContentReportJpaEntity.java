package com.example.demo.domain.report.infrastructure.persistence;

import com.example.demo.domain.report.domain.type.ReportReason;
import com.example.demo.domain.report.domain.type.ReportTargetType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "ContentReport",
    uniqueConstraints =
        @UniqueConstraint(
            name = "UQ_CONTENT_REPORT_REPORTER_TARGET",
            columnNames = {"reporterUserId", "targetType", "targetId"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContentReportJpaEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reportId")
  private Long reportId;

  @Enumerated(EnumType.STRING)
  @Column(name = "targetType", nullable = false, length = 30, columnDefinition = "VARCHAR(30)")
  private ReportTargetType targetType;

  @Column(name = "targetId", nullable = false)
  private Long targetId;

  @Column(name = "reporterUserId", nullable = false)
  private Long reporterUserId;

  @Column(name = "reportedUserId", nullable = false)
  private Long reportedUserId;

  @Enumerated(EnumType.STRING)
  @Column(name = "reason", nullable = false, length = 50, columnDefinition = "VARCHAR(50)")
  private ReportReason reason;

  @Column(name = "createdAt", nullable = false)
  private LocalDateTime createdAt;
}
