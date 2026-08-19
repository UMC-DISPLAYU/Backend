package com.example.demo.domain.display.presentation.mapper;

import com.example.demo.domain.display.application.command.CreateDisplayContentCategoryCommand;
import com.example.demo.domain.display.application.command.CreateDisplayContentCommand;
import com.example.demo.domain.display.application.command.DeleteDisplayContentCategoryCommand;
import com.example.demo.domain.display.application.command.DeleteDisplayContentCommand;
import com.example.demo.domain.display.application.command.ReorderDisplayContentsCommand;
import com.example.demo.domain.display.application.command.UpdateDisplayContentCategoryCommand;
import com.example.demo.domain.display.application.command.UpdateDisplayContentCommand;
import com.example.demo.domain.display.application.result.DeleteDisplayContentCategoryResult;
import com.example.demo.domain.display.application.result.DeleteDisplayContentResult;
import com.example.demo.domain.display.application.result.DisplayContentCategoryResult;
import com.example.demo.domain.display.application.result.DisplayContentResult;
import com.example.demo.domain.display.application.result.ReorderDisplayContentsResult;
import com.example.demo.domain.display.presentation.request.CreateDisplayContentCategoryRequest;
import com.example.demo.domain.display.presentation.request.CreateDisplayContentRequest;
import com.example.demo.domain.display.presentation.request.ReorderDisplayContentsRequest;
import com.example.demo.domain.display.presentation.request.UpdateDisplayContentCategoryRequest;
import com.example.demo.domain.display.presentation.request.UpdateDisplayContentRequest;
import com.example.demo.domain.display.presentation.response.DeleteDisplayContentCategoryResponse;
import com.example.demo.domain.display.presentation.response.DeleteDisplayContentResponse;
import com.example.demo.domain.display.presentation.response.DisplayContentCategoryResponse;
import com.example.demo.domain.display.presentation.response.DisplayContentResponse;
import com.example.demo.domain.display.presentation.response.ReorderDisplayContentsResponse;
import org.springframework.stereotype.Component;

@Component
public class DisplayContentPresentationMapper {

  public CreateDisplayContentCategoryCommand toCommand(
      CreateDisplayContentCategoryRequest request, Long userId, Long displayId) {
    return new CreateDisplayContentCategoryCommand(
        userId, displayId, request.name(), request.description());
  }

  public UpdateDisplayContentCategoryCommand toCommand(
      UpdateDisplayContentCategoryRequest request, Long userId, Long displayId, Long categoryId) {
    return new UpdateDisplayContentCategoryCommand(
        userId, displayId, categoryId, request.name(), request.description());
  }

  public DeleteDisplayContentCategoryCommand toDeleteCategoryCommand(
      Long userId, Long displayId, Long categoryId) {
    return new DeleteDisplayContentCategoryCommand(userId, displayId, categoryId);
  }

  public CreateDisplayContentCommand toCommand(
      CreateDisplayContentRequest request, Long userId, Long displayId, Long categoryId) {
    return new CreateDisplayContentCommand(userId, displayId, categoryId, request.imageUrl());
  }

  public UpdateDisplayContentCommand toCommand(
      UpdateDisplayContentRequest request,
      Long userId,
      Long displayId,
      Long categoryId,
      Long contentId) {
    return new UpdateDisplayContentCommand(
        userId, displayId, categoryId, contentId, request.imageUrl());
  }

  public DeleteDisplayContentCommand toDeleteContentCommand(
      Long userId, Long displayId, Long categoryId, Long contentId) {
    return new DeleteDisplayContentCommand(userId, displayId, categoryId, contentId);
  }

  public ReorderDisplayContentsCommand toCommand(
      ReorderDisplayContentsRequest request, Long userId, Long displayId, Long categoryId) {
    return new ReorderDisplayContentsCommand(
        userId, displayId, categoryId, request.orderedContentIds());
  }

  public DisplayContentCategoryResponse toResponse(DisplayContentCategoryResult result) {
    return new DisplayContentCategoryResponse(
        result.displayId(),
        result.categoryId(),
        result.name(),
        result.description(),
        result.sortOrder(),
        result.contents().stream().map(this::toResponse).toList());
  }

  public DisplayContentResponse toResponse(DisplayContentResult result) {
    return new DisplayContentResponse(
        result.categoryId(),
        result.contentId(),
        result.userId(),
        result.imageUrl(),
        result.sortOrder());
  }

  public DeleteDisplayContentCategoryResponse toResponse(
      DeleteDisplayContentCategoryResult result) {
    return new DeleteDisplayContentCategoryResponse(result.displayId(), result.categoryId());
  }

  public DeleteDisplayContentResponse toResponse(DeleteDisplayContentResult result) {
    return new DeleteDisplayContentResponse(
        result.displayId(), result.categoryId(), result.contentId());
  }

  public ReorderDisplayContentsResponse toResponse(ReorderDisplayContentsResult result) {
    return new ReorderDisplayContentsResponse(
        result.displayId(),
        result.categoryId(),
        result.contents().stream().map(this::toResponse).toList());
  }
}
