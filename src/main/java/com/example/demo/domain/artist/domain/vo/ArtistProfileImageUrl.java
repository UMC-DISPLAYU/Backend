package com.example.demo.domain.artist.domain.vo;

import static com.example.demo.global.util.StringNormalizer.normalize;

import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;

public record ArtistProfileImageUrl(String value) {

  private static final int MAX_LENGTH = 2048;
  private static final String CDN_PREFIX = "https://d1tdgnysscm2va.cloudfront.net/";

  public ArtistProfileImageUrl {
    if (value != null && (value.length() > MAX_LENGTH || !value.startsWith(CDN_PREFIX))) {
      throw new ArtistException(ArtistErrorCode.INVALID_PROFILE_IMAGE_URL);
    }
  }

  public static ArtistProfileImageUrl ofNullable(String value) {
    return new ArtistProfileImageUrl(normalize(value));
  }
}
