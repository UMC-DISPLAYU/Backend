package com.example.demo.domain.personalartwork.application.usecase;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkAccessResult;
import java.util.Optional;

public interface GetPersonalArtworkAccessUseCase {

  Optional<PersonalArtworkAccessResult> getPersonalArtworkAccess(Long personalArtworkId);
}
