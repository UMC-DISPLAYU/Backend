package com.example.demo.domain.user.domain.vo;

import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import java.util.regex.Pattern;

public record Nickname(String value) {

  private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]{2,15}$");

  public Nickname {
    validate(value);
  }

  public static Nickname of(String value) {
    return new Nickname(value);
  }

  private static void validate(String value) {
    if (value == null || !NICKNAME_PATTERN.matcher(value).matches()) {
      throw new UserException(UserErrorCode.INVALID_NICKNAME_FORMAT);
    }
  }
}
