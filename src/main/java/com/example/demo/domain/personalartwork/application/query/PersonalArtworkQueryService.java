package com.example.demo.domain.personalartwork.application.query;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkResult;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.error.PersonalArtworkErrorCode;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonalArtworkQueryService {

  private final PersonalArtworkRepository personalArtworkRepository;

  public PersonalArtworkQueryService(PersonalArtworkRepository personalArtworkRepository) {
    this.personalArtworkRepository = personalArtworkRepository;
  }

  @Transactional(readOnly = true)
  public PersonalArtworkResult getPersonalArtworkDetail(Long personalArtworkId) {
    return PersonalArtworkResult.from(getPersonalArtwork(personalArtworkId));
  }

  @Transactional(readOnly = true)
  public PersonalArtworkResult getOwnedPersonalArtworkDetail(
      Long personalArtworkId, Long requesterUserId) {
    PersonalArtwork personalArtwork = getPersonalArtwork(personalArtworkId);
    if (!personalArtwork.isOwnedBy(requesterUserId)) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }
    return PersonalArtworkResult.from(personalArtwork);
  }

  private PersonalArtwork getPersonalArtwork(Long personalArtworkId) {
    return personalArtworkRepository
        .findById(personalArtworkId)
        .filter(artwork -> !artwork.isDeleted())
        .orElseThrow(
            () -> new BusinessException(PersonalArtworkErrorCode.PERSONAL_ARTWORK_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public List<PersonalArtworkSummaryResult> getPersonalArtworksByUser(Long userId) {
    return personalArtworkRepository.findAllByOwnerUserIdOrderByCreatedAtAsc(userId).stream()
        .map(PersonalArtworkSummaryResult::from)
        .toList();
  }
}
