package com.example.demo.domain.report;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.domain.report.domain.model.ContentReport;
import com.example.demo.domain.report.domain.type.ReportReason;
import com.example.demo.domain.report.domain.type.ReportTargetType;
import com.example.demo.domain.report.infrastructure.persistence.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class JpaContentReportRepositoryAdapterTest {
  @Test
  void unrelatedIntegrityFailureIsNotReportedAsDuplicate() {
    var repository = mock(SpringDataContentReportJpaRepository.class);
    var mapper = mock(ContentReportPersistenceMapper.class);
    var exception =
        new DataIntegrityViolationException(
            "invalid column",
            new ConstraintViolationException(
                "null reason", new SQLException("not null"), "ContentReport.reason"));
    when(repository.saveAndFlush(any())).thenThrow(exception);
    var adapter = new JpaContentReportRepositoryAdapter(repository, mapper);
    ContentReport report =
        ContentReport.create(
            ReportTargetType.DISPLAY_REVIEW,
            1L,
            2L,
            3L,
            ReportReason.OTHER,
            LocalDateTime.of(2026, 11, 17, 0, 0));
    assertThatThrownBy(() -> adapter.save(report)).isSameAs(exception);
  }
}
