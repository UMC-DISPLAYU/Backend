package com.example.demo.domain.user.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NicknameTest {

  @ParameterizedTest
  @ValueSource(strings = {"가", "abcdefghijklmnop", "닉네임 공백", "닉네임!", "닉네임😀"})
  void rejectsInvalidNickname(String value) {
    assertThatExceptionOfType(UserException.class)
        .isThrownBy(() -> Nickname.of(value))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(UserErrorCode.INVALID_NICKNAME_FORMAT));
  }

  @ParameterizedTest
  @ValueSource(strings = {"가나", "abcdefghijklmno", "작가Artist01"})
  void acceptsValidNickname(String value) {
    assertThat(Nickname.of(value).value()).isEqualTo(value);
  }
}
