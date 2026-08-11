package com.example.demo.domain.personalartwork.application.command;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkLikeResult;
import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkLike;
import com.example.demo.domain.personalartwork.domain.error.PersonalArtworkErrorCode;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkLikeRepository;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import com.example.demo.global.error.BusinessException;
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

    if (personalArtworkLikeRepository
        .findByPersonalArtworkIdAndUserId(command.personalArtworkId(), command.userId())
        .isPresent()) {
      return result(command.personalArtworkId(), true);
    }

    PersonalArtworkLike personalArtworkLike =
        PersonalArtworkLike.create(command.personalArtworkId(), command.userId());

    try {
      personalArtworkLikeRepository.save(personalArtworkLike);
    } catch (DataIntegrityViolationException exception) {
      return result(command.personalArtworkId(), true);
    }

    return result(command.personalArtworkId(), true);
  }

  @Transactional
  public PersonalArtworkLikeResult cancel(PersonalArtworkLikeCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    validatePersonalArtworkExists(command.personalArtworkId());

    int deleted =
        personalArtworkLikeRepository.deleteByPersonalArtworkIdAndUserId(
            command.personalArtworkId(), command.userId());
    if (deleted == 0) {
      throw new BusinessException(PersonalArtworkErrorCode.PERSONAL_ARTWORK_LIKE_NOT_FOUND);
    }
    return result(command.personalArtworkId(), false);
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
        personalArtworkLikeRepository.countByPersonalArtworkId(personalArtworkId));
  }
}
