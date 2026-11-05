package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.ContentDeletionResult;
import com.example.demo.domain.admin.presentation.response.ContentDeletionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminContentDeletionMapper {
  ContentDeletionResponse toResponse(ContentDeletionResult result);
}
