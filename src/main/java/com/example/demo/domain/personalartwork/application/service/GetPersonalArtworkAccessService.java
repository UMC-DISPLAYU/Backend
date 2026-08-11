package com.example.demo.domain.personalartwork.application.service;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkAccessResult;
import com.example.demo.domain.personalartwork.application.usecase.GetPersonalArtworkAccessUseCase;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPersonalArtworkAccessService implements GetPersonalArtworkAccessUseCase {

  private final PersonalArtworkRepository personalArtworkRepository;

  @Override
  @Transactional(readOnly = true)
  public Optional<PersonalArtworkAccessResult> getPersonalArtworkAccess(Long personalArtworkId) {
    return personalArtworkRepository
        .findById(personalArtworkId)
        .filter(personalArtwork -> !personalArtwork.isDeleted())
        .map(PersonalArtworkAccessResult::from);
  }
}
