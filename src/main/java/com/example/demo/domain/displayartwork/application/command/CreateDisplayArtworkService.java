package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.ArtistVerificationRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateDisplayArtworkService {

  // TODO: PM 확답되면 정확한 값으로 조정 (현재 "30~40선" 범위 중 상한값으로 임시 설정)
  private static final int MAX_ARTWORKS_PER_DISPLAY = 40;

  private final DisplayRepository displayRepository;
  private final DisplayArtworkRepository displayArtworkRepository;
  private final ArtistVerificationRepository artistVerificationRepository;

  public CreateDisplayArtworkService(
      DisplayRepository displayRepository,
      DisplayArtworkRepository displayArtworkRepository,
      ArtistVerificationRepository artistVerificationRepository) {
    this.displayRepository = displayRepository;
    this.displayArtworkRepository = displayArtworkRepository;
    this.artistVerificationRepository = artistVerificationRepository;
  }

  @Transactional
  public Long createDisplayArtwork(Long requesterUserId, CreateDisplayArtworkCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display =
        displayRepository
            .findById(command.displayId())
            .orElseThrow(() -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_NOT_FOUND));

    validateTeamMember(display, requesterUserId);
    validateArtworkLimit(command.displayId());

    int nextWorkSortOrder = displayArtworkRepository.countByDisplayId(command.displayId());

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
            toImages(command.images()));

    DisplayArtwork savedDisplayArtwork = displayArtworkRepository.save(displayArtwork);
    return savedDisplayArtwork.getId();
  }

  private void validateTeamMember(Display display, Long requesterUserId) {
    boolean isAcceptedTeamMember =
        display.getTeamMembers().stream()
            .anyMatch(
                teamMember ->
                    teamMember.isAccepted()
                        && teamMember.getUserId().value().equals(requesterUserId));
    if (!isAcceptedTeamMember) {
      throw new BusinessException(DisplayArtworkErrorCode.NOT_DISPLAY_TEAM_MEMBER);
    }
    if (!artistVerificationRepository.isVerifiedArtist(requesterUserId)) {
      throw new BusinessException(DisplayArtworkErrorCode.NOT_VERIFIED_ARTIST);
    }
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
}
