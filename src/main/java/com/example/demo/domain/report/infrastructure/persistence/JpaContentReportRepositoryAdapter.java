package com.example.demo.domain.report.infrastructure.persistence;

import com.example.demo.domain.report.domain.error.ReportErrorCode;
import com.example.demo.domain.report.domain.model.ContentReport;
import com.example.demo.domain.report.domain.repository.ContentReportRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaContentReportRepositoryAdapter implements ContentReportRepository {
  private static final String DUPLICATE_CONSTRAINT = "UQ_CONTENT_REPORT_REPORTER_TARGET";
  private final SpringDataContentReportJpaRepository repository;
  private final ContentReportPersistenceMapper mapper;

  @Override
  public ContentReport save(ContentReport report) {
    try {
      return mapper.toDomain(repository.saveAndFlush(mapper.toEntity(report)));
    } catch (DataIntegrityViolationException exception) {
      for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
        if (cause instanceof ConstraintViolationException violation
            && violation.getConstraintName() != null
            && violation
                .getConstraintName()
                .toUpperCase(Locale.ROOT)
                .contains(DUPLICATE_CONSTRAINT)) {
          throw new BusinessException(ReportErrorCode.CONTENT_REPORT_ALREADY_EXISTS, exception);
        }
      }
      throw exception;
    }
  }
}
