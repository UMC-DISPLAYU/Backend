package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.displayartwork.application.permission.DisplayArtworkPermissionChecker;
import com.example.demo.domain.displayartwork.application.result.AuthorSetupResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkResult;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateDisplayArtworkService {

  private static final int MAX_ARTWORKS_PER_DISPLAY = 50;

  private final DisplayRepository displayRepository;
  private final DisplayArtworkRepository displayArtworkRepository;
  private final DisplayArtworkPermissionChecker permissionChecker;
  private final AuthorSetupService authorSetupService;
  private final Clock clock;

  public CreateDisplayArtworkService(
      DisplayRepository displayRepository,
      DisplayArtworkRepository displayArtworkRepository,
      DisplayArtworkPermissionChecker permissionChecker,
      AuthorSetupService authorSetupService,
      Clock clock) {
    this.displayRepository = displayRepository;
    this.displayArtworkRepository = displayArtworkRepository;
    this.permissionChecker = permissionChecker;
    this.authorSetupService = authorSetupService;
    this.clock = clock;
  }

  @Transactional
  public DisplayArtworkResult createDisplayArtwork(
      Long requesterUserId, CreateDisplayArtworkCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display =
        displayRepository
            .findById(command.displayId())
            .orElseThrow(() -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_NOT_FOUND));

    permissionChecker.requireArtworkRegistrant(requesterUserId, display);
    validateArtworkLimit(command.displayId());

    int nextWorkSortOrder =
        displayArtworkRepository
            .findMaxWorkSortOrderByDisplayId(command.displayId())
            .map(max -> max + 1)
            .orElse(0);

    DisplayArtwork displayArtwork =
        DisplayArtwork.create(
            display,
            command.artworkName(),
            command.content(),
            command.type(),
            command.productionYear(),
            command.materialMedia(),
            command.size(),
            command.point(),
            nextWorkSortOrder,
            requesterUserId,
            initialArtworkStatus(display),
            toImages(command.images()));

    DisplayArtwork savedDisplayArtwork = displayArtworkRepository.save(displayArtwork);

    AuthorSetupResult authorSetupResult =
        authorSetupService.setup(
            requesterUserId,
            new AuthorSetupCommand(
                savedDisplayArtwork.getId(),
                command.artistName(),
                command.artistUserId(),
                command.coAuthorUserIds(),
                command.coAuthorRawNames(),
                command.qaHandlerUserIds()));

    return DisplayArtworkResult.of(savedDisplayArtwork, authorSetupResult);
  }

  private void validateArtworkLimit(Long displayId) {
    if (displayArtworkRepository.countByDisplayId(displayId) >= MAX_ARTWORKS_PER_DISPLAY) {
      throw new BusinessException(DisplayArtworkErrorCode.ARTWORK_REGISTRATION_LIMIT_EXCEEDED);
    }
  }

  private List<ArtworkImage> toImages(List<ArtworkImageCommand> imageCommands) {
    Objects.requireNonNull(imageCommands, "images must not be null.");
    return imageCommands.stream()
        .map(
            image ->
                new ArtworkImage(
                    null,
                    image.imageUrl(),
                    image.isThumbnail(),
                    image.imageType(),
                    image.sortOrder(),
                    image.caption(),
                    image.width(),
                    image.height()))
        .toList();
  }

  private DisplayArtworkStatus initialArtworkStatus(Display display) {
    if (display.getStatus() != DisplayStatus.PUBLISHED) {
      return DisplayArtworkStatus.DRAFT;
    }
    if (display.getArtworkContentOpen() == ContentOpenPolicy.IMMEDIATELY
        || !display.getPeriod().startDate().isAfter(LocalDate.now(clock))) {
      return DisplayArtworkStatus.PUBLISHED;
    }
    return DisplayArtworkStatus.DRAFT;
  }
}
