package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.displayartwork.application.result.AuthorSetupResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkResult;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateDisplayArtworkService {

  private final DisplayArtworkRepository displayArtworkRepository;
  private final ArtworkEditPermission artworkEditPermission;
  private final AuthorSetupService authorSetupService;

  public UpdateDisplayArtworkService(
      DisplayArtworkRepository displayArtworkRepository,
      ArtworkEditPermission artworkEditPermission,
      AuthorSetupService authorSetupService) {
    this.displayArtworkRepository = displayArtworkRepository;
    this.artworkEditPermission = artworkEditPermission;
    this.authorSetupService = authorSetupService;
  }

  @Transactional
  public DisplayArtworkResult update(Long requesterUserId, UpdateDisplayArtworkCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    DisplayArtwork artwork =
        displayArtworkRepository
            .findById(command.artworkId())
            .filter(a -> !a.isDeleted())
            .orElseThrow(
                () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));

    if (!artworkEditPermission.canEdit(
        artwork.getDisplay(), command.artworkId(), requesterUserId)) {
      throw new BusinessException(DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_EDIT);
    }

    artwork.changeContent(
        command.artworkName(),
        command.content(),
        command.type(),
        command.productionYear(),
        command.materialMedia(),
        command.size(),
        command.point());
    // 화면에서 최종적으로 남은 이미지 목록을 그대로 저장한다.
    artwork.replaceImages(toImages(command.images()));

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

    return DisplayArtworkResult.of(artwork, authorSetupResult);
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
}
