package com.example.demo.domain.user.domain.vo;

import static com.example.demo.global.util.StringNormalizer.normalize;

import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;

public record ProfileImageUrl(String value) {

  private static final int MAX_LENGTH = 2048;
  private static final String CDN_PREFIX = "https://d1tdgnysscm2va.cloudfront.net/";

  public ProfileImageUrl {
    validate(value);
  }

  public static ProfileImageUrl ofNullable(String value) {
    return new ProfileImageUrl(normalize(value));
  }

  private static void validate(String value) {
    if (value == null) {
      return;
    }
    if (value.length() > MAX_LENGTH || !value.startsWith(CDN_PREFIX)) {
      throw new UserException(UserErrorCode.INVALID_PROFILE_IMAGE_URL);
    }
  }
}
