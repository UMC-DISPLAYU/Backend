package com.example.demo.domain.artist.application.permission;

import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import org.springframework.stereotype.Component;

@Component
public class ArtistPermissionChecker {

  public void requireProfileCreationEligible(User user) {
    if (!user.isVerified()
        || user.getSchoolEmail() == null
        || user.getSchoolEmail().isBlank()
        || user.getUnivName() == null
        || user.getUnivName().isBlank()) {
      throw new ArtistException(ArtistErrorCode.ARTIST_PROFILE_REQUIRES_VERIFIED_USER);
    }
  }

  public void requireVerified(User user) {
    if (!user.isVerified()) {
      throw new UserException(UserErrorCode.ARTIST_VERIFICATION_REQUIRED);
    }
  }
}
