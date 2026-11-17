package com.example.demo.domain.report.infrastructure.persistence;

import com.example.demo.domain.report.domain.model.ContentReport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContentReportPersistenceMapper {
  ContentReportJpaEntity toEntity(ContentReport report);

  ContentReport toDomain(ContentReportJpaEntity entity);
}
