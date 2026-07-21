package com.example.demo.domain.user.domain.vo;

import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import java.net.URI;
import java.net.URISyntaxException;

public record ProfileImageUrl(String value) {

  private static final int MAX_LENGTH = 2048;

  public ProfileImageUrl {
    validate(value);
  }

  public static ProfileImageUrl ofNullable(String value) {
    return new ProfileImageUrl(normalize(value));
  }

  private static String normalize(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }

  private static void validate(String value) {
    if (value == null) {
      return;
    }
    if (value.length() > MAX_LENGTH) {
      throw new UserException(UserErrorCode.INVALID_PROFILE_IMAGE_URL);
    }
    try {
      URI uri = new URI(value);
      if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
          || uri.getHost() == null) {
        throw new UserException(UserErrorCode.INVALID_PROFILE_IMAGE_URL);
      }
    } catch (URISyntaxException exception) {
      throw new UserException(UserErrorCode.INVALID_PROFILE_IMAGE_URL);
    }
  }
}
