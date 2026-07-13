package com.example.demo.domain.personalartwork.application.command;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkImage;
import com.example.demo.domain.personalartwork.domain.error.PersonalArtworkErrorCode;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import com.example.demo.domain.personalartwork.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonalArtworkCommandService {

  private final PersonalArtworkRepository personalArtworkRepository;

  public PersonalArtworkCommandService(PersonalArtworkRepository personalArtworkRepository) {
    this.personalArtworkRepository = personalArtworkRepository;
  }

  @Transactional
  public Long createPersonalArtwork(Long ownerUserId, PersonalArtworkContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    int nextSortOrder = personalArtworkRepository.countByOwnerUserId(ownerUserId);
    PersonalArtwork personalArtwork =
        PersonalArtwork.create(
            new UserId(ownerUserId),
            command.artworkName(),
            command.content(),
            command.type(),
            command.productionYear(),
            command.materialMedia(),
            command.size(),
            command.point(),
            nextSortOrder,
            toImages(command.images()));

    PersonalArtwork savedPersonalArtwork = personalArtworkRepository.save(personalArtwork);
    return savedPersonalArtwork.getId();
  }

  @Transactional
  public void updatePersonalArtwork(
      Long personalArtworkId, Long requesterUserId, PersonalArtworkContentCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    PersonalArtwork personalArtwork = getOwnedPersonalArtwork(personalArtworkId, requesterUserId);
    personalArtwork.changeContent(
        command.artworkName(),
        command.content(),
        command.type(),
        command.productionYear(),
        command.materialMedia(),
        command.size(),
        command.point());
    personalArtwork.replaceImages(toImages(command.images()));
  }

  @Transactional
  public void deletePersonalArtwork(Long personalArtworkId, Long requesterUserId) {
    PersonalArtwork personalArtwork = getOwnedPersonalArtwork(personalArtworkId, requesterUserId);
    personalArtwork.delete();
  }

  private PersonalArtwork getOwnedPersonalArtwork(Long personalArtworkId, Long requesterUserId) {
    PersonalArtwork personalArtwork =
        personalArtworkRepository
            .findById(personalArtworkId)
            .filter(artwork -> !artwork.isDeleted())
            .orElseThrow(
                () -> new BusinessException(PersonalArtworkErrorCode.PERSONAL_ARTWORK_NOT_FOUND));
    if (!personalArtwork.isOwnedBy(requesterUserId)) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }
    return personalArtwork;
  }

  private List<PersonalArtworkImage> toImages(List<PersonalArtworkImageCommand> imageCommands) {
    Objects.requireNonNull(imageCommands, "images must not be null.");
    return imageCommands.stream()
        .map(
            image ->
                new PersonalArtworkImage(
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
