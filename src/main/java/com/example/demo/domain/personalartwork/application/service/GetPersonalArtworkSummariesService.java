package com.example.demo.domain.personalartwork.application.service;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import com.example.demo.domain.personalartwork.application.usecase.GetPersonalArtworkSummariesUseCase;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPersonalArtworkSummariesService implements GetPersonalArtworkSummariesUseCase {

  private final PersonalArtworkRepository personalArtworkRepository;

  public GetPersonalArtworkSummariesService(PersonalArtworkRepository personalArtworkRepository) {
    this.personalArtworkRepository = personalArtworkRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<PersonalArtworkSummaryResult> getPersonalArtworkSummaries(
      List<Long> personalArtworkIds) {
    if (personalArtworkIds.isEmpty()) {
      return List.of();
    }
    return personalArtworkRepository.findAllByIdInAndDeletedAtIsNull(personalArtworkIds).stream()
        .map(PersonalArtworkSummaryResult::from)
        .toList();
  }
}
