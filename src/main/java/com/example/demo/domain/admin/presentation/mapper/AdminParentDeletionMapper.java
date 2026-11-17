package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.ParentDeletionResult;
import com.example.demo.domain.admin.presentation.response.ParentDeletionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminParentDeletionMapper {
  ParentDeletionResponse toResponse(ParentDeletionResult result);
}
