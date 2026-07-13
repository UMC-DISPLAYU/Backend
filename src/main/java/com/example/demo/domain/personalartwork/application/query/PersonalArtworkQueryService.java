package com.example.demo.domain.personalartwork.application.query;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkResult;
import com.example.demo.domain.personalartwork.domain.error.PersonalArtworkErrorCode;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import com.example.demo.global.error.BusinessException;
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
    return personalArtworkRepository
        .findById(personalArtworkId)
        .filter(artwork -> !artwork.isDeleted())
        .map(PersonalArtworkResult::from)
        .orElseThrow(
            () -> new BusinessException(PersonalArtworkErrorCode.PERSONAL_ARTWORK_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public List<PersonalArtworkResult> getPersonalArtworksByUser(Long userId) {
    return personalArtworkRepository.findAllByOwnerUserIdOrderBySortOrderAsc(userId).stream()
        .map(PersonalArtworkResult::from)
        .toList();
  }
}
