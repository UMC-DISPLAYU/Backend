package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.result.DeleteDisplayContentCategoryResult;
import com.example.demo.domain.display.application.result.DeleteDisplayContentResult;
import com.example.demo.domain.display.application.result.DisplayContentCategoryResult;
import com.example.demo.domain.display.application.result.DisplayContentResult;
import com.example.demo.domain.display.application.result.ReorderDisplayContentsResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayContent;
import com.example.demo.domain.display.domain.entity.DisplayContentCategory;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayContentStatus;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.global.error.BusinessException;
import jakarta.persistence.OptimisticLockException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisplayContentCommandService {

  private final DisplayRepository displayRepository;
  private final Clock clock;

  public DisplayContentCommandService(DisplayRepository displayRepository, Clock clock) {
    this.displayRepository = displayRepository;
    this.clock = clock;
  }

  @Transactional
  public DisplayContentCategoryResult createCategory(CreateDisplayContentCategoryCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display = findDisplay(command.displayId());
    validateContentEditor(display, command.userId());
    DisplayContentCategory category =
        display.createContentCategory(command.name(), command.description());
    displayRepository.flush();

    return DisplayContentCategoryResult.from(category);
  }

  @Transactional
  public DisplayContentCategoryResult updateCategory(UpdateDisplayContentCategoryCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display = findDisplay(command.displayId());
    validateContentEditor(display, command.userId());
    DisplayContentCategory category =
        display.changeContentCategory(command.categoryId(), command.name(), command.description());

    return DisplayContentCategoryResult.from(category);
  }

  @Transactional
  public DeleteDisplayContentCategoryResult deleteCategory(
      DeleteDisplayContentCategoryCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display = findDisplay(command.displayId());
    validateContentEditor(display, command.userId());
    display.removeContentCategory(command.categoryId());

    return new DeleteDisplayContentCategoryResult(command.displayId(), command.categoryId());
  }

  @Transactional
  public DisplayContentResult createContent(CreateDisplayContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display = findDisplay(command.displayId());
    validateContentEditor(display, command.userId());
    DisplayContent content =
        display.createContent(
            command.categoryId(),
            command.imageUrl(),
            command.width(),
            command.height(),
            initialContentStatus(display));
    displayRepository.flush();

    return DisplayContentResult.from(content);
  }

  @Transactional
  public DisplayContentResult updateContent(UpdateDisplayContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display = findDisplay(command.displayId());
    validateContentEditor(display, command.userId());
    DisplayContent content =
        display.changeContent(
            command.categoryId(),
            command.contentId(),
            command.imageUrl(),
            command.width(),
            command.height());

    return DisplayContentResult.from(content);
  }

  @Transactional
  public DeleteDisplayContentResult deleteContent(DeleteDisplayContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display = findDisplay(command.displayId());
    validateContentEditor(display, command.userId());
    display.removeContent(command.categoryId(), command.contentId());

    return new DeleteDisplayContentResult(
        command.displayId(), command.categoryId(), command.contentId());
  }

  @Transactional
  public ReorderDisplayContentsResult reorderContents(ReorderDisplayContentsCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    try {
      Display display = findDisplayWithOptimisticLock(command.displayId());
      validateContentEditor(display, command.userId());
      List<DisplayContent> contents =
          display.reorderContents(command.categoryId(), command.orderedContentIds());
      displayRepository.flush();

      return ReorderDisplayContentsResult.from(command.displayId(), command.categoryId(), contents);
    } catch (OptimisticLockingFailureException | OptimisticLockException e) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_CONTENT_REORDER_CONFLICT, e);
    }
  }

  private Display findDisplay(Long displayId) {
    return displayRepository
        .findById(displayId)
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));
  }

  private Display findDisplayWithOptimisticLock(Long displayId) {
    return displayRepository
        .findByIdWithOptimisticLock(displayId)
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));
  }

  private void validateContentEditor(Display display, Long userId) {
    if (!display.hasAcceptedTeamMember(userId)) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_CONTENT_PERMISSION_DENIED);
    }
  }

  private DisplayContentStatus initialContentStatus(Display display) {
    if (display.getStatus() != DisplayStatus.PUBLISHED) {
      return DisplayContentStatus.DRAFT;
    }
    if (display.getExhibitionContentOpen() == ContentOpenPolicy.IMMEDIATELY
        || !display.getPeriod().startDate().isAfter(LocalDate.now(clock))) {
      return DisplayContentStatus.PUBLISHED;
    }
    return DisplayContentStatus.DRAFT;
  }
}
