package com.example.demo.domain.personalartwork.application.command;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkLikeResult;
import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkLike;
import com.example.demo.domain.personalartwork.domain.error.PersonalArtworkErrorCode;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkLikeRepository;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonalArtworkLikeCommandService {

  private final PersonalArtworkRepository personalArtworkRepository;
  private final PersonalArtworkLikeRepository personalArtworkLikeRepository;

  public PersonalArtworkLikeCommandService(
      PersonalArtworkRepository personalArtworkRepository,
      PersonalArtworkLikeRepository personalArtworkLikeRepository) {
    this.personalArtworkRepository = personalArtworkRepository;
    this.personalArtworkLikeRepository = personalArtworkLikeRepository;
  }

  @Transactional
  public PersonalArtworkLikeResult like(PersonalArtworkLikeCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    validatePersonalArtworkExists(command.personalArtworkId());

    PersonalArtworkLike personalArtworkLike =
        personalArtworkLikeRepository
            .findByPersonalArtworkIdAndUserId(command.personalArtworkId(), command.userId())
            .map(this::restoreDeletedLike)
            .orElseGet(
                () -> PersonalArtworkLike.create(command.personalArtworkId(), command.userId()));

    try {
      personalArtworkLikeRepository.save(personalArtworkLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(command.personalArtworkId(), true);
  }

  @Transactional
  public PersonalArtworkLikeResult cancel(PersonalArtworkLikeCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    validatePersonalArtworkExists(command.personalArtworkId());

    PersonalArtworkLike personalArtworkLike =
        personalArtworkLikeRepository
            .findByPersonalArtworkIdAndUserId(command.personalArtworkId(), command.userId())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    personalArtworkLike.cancel();
    return result(command.personalArtworkId(), false);
  }

  private PersonalArtworkLike restoreDeletedLike(PersonalArtworkLike personalArtworkLike) {
    if (personalArtworkLike.isActive()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    personalArtworkLike.restore();
    return personalArtworkLike;
  }

  private void validatePersonalArtworkExists(Long personalArtworkId) {
    personalArtworkRepository
        .findById(personalArtworkId)
        .filter(artwork -> !artwork.isDeleted())
        .orElseThrow(
            () -> new BusinessException(PersonalArtworkErrorCode.PERSONAL_ARTWORK_NOT_FOUND));
  }

  private PersonalArtworkLikeResult result(Long personalArtworkId, boolean isLiked) {
    return new PersonalArtworkLikeResult(
        personalArtworkId,
        isLiked,
        personalArtworkLikeRepository.countByPersonalArtworkIdAndDeletedAtIsNull(
            personalArtworkId));
  }
}
