package com.example.demo.domain.artist.application.permission;

import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;
import com.example.demo.domain.artist.domain.model.ArtistUserQualification;
import org.springframework.stereotype.Component;

@Component
public class ArtistPermissionChecker {

  public void requireProfileCreationEligible(ArtistUserQualification qualification) {
    if (!qualification.isSchoolVerified() || qualification.isWithdrawn()) {
      throw new ArtistException(ArtistErrorCode.ARTIST_PROFILE_REQUIRES_VERIFIED_USER);
    }
  }

  public void requireVerified(ArtistUserQualification qualification) {
    if (!qualification.isSchoolVerified() || qualification.isWithdrawn()) {
      throw new ArtistException(ArtistErrorCode.ARTIST_VERIFICATION_REQUIRED);
    }
  }
}
