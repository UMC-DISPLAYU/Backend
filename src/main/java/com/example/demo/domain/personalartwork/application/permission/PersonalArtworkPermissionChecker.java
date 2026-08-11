package com.example.demo.domain.personalartwork.application.permission;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.error.PersonalArtworkErrorCode;
import com.example.demo.domain.personalartwork.domain.repository.ArtistVerificationRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonalArtworkPermissionChecker {

  private final ArtistVerificationRepository artistVerificationRepository;

  public void requireVerifiedArtist(Long userId) {
    if (!artistVerificationRepository.isVerifiedArtist(userId)) {
      throw new BusinessException(PersonalArtworkErrorCode.NOT_VERIFIED_ARTIST);
    }
  }

  public void requireOwner(PersonalArtwork personalArtwork, Long userId) {
    if (!personalArtwork.isOwnedBy(userId)) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }
  }
}
