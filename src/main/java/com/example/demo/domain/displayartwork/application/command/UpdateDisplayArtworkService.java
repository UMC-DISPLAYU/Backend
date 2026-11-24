package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.displayartwork.application.permission.DisplayArtworkPermissionChecker;
import com.example.demo.domain.displayartwork.application.result.AuthorSetupResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkResult;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.file.application.MediaUsage;
import com.example.demo.global.file.application.event.MediaFilesRemovedEvent;
import com.example.demo.global.file.application.port.MediaStorageVerifier;
import java.util.List;
import java.util.Objects;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateDisplayArtworkService {

  private final DisplayArtworkRepository displayArtworkRepository;
  private final DisplayArtworkPermissionChecker permissionChecker;
  private final AuthorSetupService authorSetupService;
  private final MediaStorageVerifier mediaStorageVerifier;
  private final ApplicationEventPublisher eventPublisher;

  public UpdateDisplayArtworkService(
      DisplayArtworkRepository displayArtworkRepository,
      DisplayArtworkPermissionChecker permissionChecker,
      AuthorSetupService authorSetupService,
      MediaStorageVerifier mediaStorageVerifier,
      ApplicationEventPublisher eventPublisher) {
    this.displayArtworkRepository = displayArtworkRepository;
    this.permissionChecker = permissionChecker;
    this.authorSetupService = authorSetupService;
    this.mediaStorageVerifier = mediaStorageVerifier;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public DisplayArtworkResult update(Long requesterUserId, UpdateDisplayArtworkCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    DisplayArtwork artwork =
        displayArtworkRepository
            .findById(command.artworkId())
            .filter(a -> !a.isDeleted() && !a.getDisplay().isDeleted())
            .orElseThrow(
                () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));

    permissionChecker.requireArtworkEditor(
        requesterUserId, artwork.getDisplay(), command.artworkId());

    command
        .images()
        .forEach(image -> mediaStorageVerifier.verifyVideo(image.media(), MediaUsage.ARTWORK));
    List<String> previousFileUrls = artwork.getMediaFileUrls();
    artwork.replaceImages(toImages(command.images()), command.artworkCrop());
    artwork.fitRepresentativeImage(command.artworkFit());

    artwork.changeContent(
        command.artworkName(),
        command.content(),
        command.types(),
        command.productionYear(),
        command.materialMedia(),
        command.size(),
        command.point());

    AuthorSetupResult authorSetupResult =
        authorSetupService.setupForUpdate(
            requesterUserId,
            new AuthorSetupCommand(
                command.artworkId(),
                command.artistName(),
                command.artistUserId(),
                command.coAuthorUserIds(),
                command.coAuthorRawNames(),
                command.qaHandlerUserIds()));

    MediaFilesRemovedEvent removed =
        MediaFilesRemovedEvent.between(previousFileUrls, artwork.getMediaFileUrls());
    if (!removed.fileUrls().isEmpty()) {
      eventPublisher.publishEvent(removed);
    }
    return DisplayArtworkResult.of(artwork, authorSetupResult);
  }

  private List<ArtworkImage> toImages(List<ArtworkImageCommand> imageCommands) {
    Objects.requireNonNull(imageCommands, "images must not be null.");
    return imageCommands.stream()
        .map(
            image ->
                new ArtworkImage(
                    null,
                    image.media(),
                    image.isThumbnail(),
                    image.imageType(),
                    image.sortOrder(),
                    image.caption(),
                    image.width(),
                    image.height()))
        .toList();
  }
}
