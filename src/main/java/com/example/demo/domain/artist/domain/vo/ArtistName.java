package com.example.demo.domain.artist.domain.vo;

import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;
import java.util.regex.Pattern;

public record ArtistName(String value) {

  public static final String PATTERN_VALUE = "^[가-힣a-zA-Z0-9]{2,15}$";
  public static final String INVALID_MESSAGE = "작가명은 한글, 영문, 숫자로 2~15자여야 하며 공백과 특수문자는 사용할 수 없습니다.";

  private static final Pattern ARTIST_NAME_PATTERN = Pattern.compile(PATTERN_VALUE);

  public ArtistName {
    if (value == null || !ARTIST_NAME_PATTERN.matcher(value).matches()) {
      throw new ArtistException(ArtistErrorCode.INVALID_ARTIST_NAME);
    }
  }

  public static ArtistName of(String value) {
    return new ArtistName(value);
  }
}
