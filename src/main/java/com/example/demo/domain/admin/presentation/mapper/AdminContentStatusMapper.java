package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.ContentStatusResult;
import com.example.demo.domain.admin.presentation.response.ContentStatusResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminContentStatusMapper {
  ContentStatusResponse toResponse(ContentStatusResult result);
}
