package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.OperationLog;
import com.example.demo.domain.admin.application.result.OperationLogPage;
import com.example.demo.domain.admin.presentation.response.OperationLogResponses;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminOperationLogMapper {
  OperationLogResponses.Page toResponse(OperationLogPage result);

  OperationLogResponses.Item toResponse(OperationLog result);
}
