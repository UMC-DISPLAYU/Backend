package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.CommentDeletionResult;
import com.example.demo.domain.admin.presentation.response.CommentDeletionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminCommentDeletionMapper {
  CommentDeletionResponse toResponse(CommentDeletionResult result);
}
