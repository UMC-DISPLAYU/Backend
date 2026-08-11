package com.example.demo.domain.user.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import org.junit.jupiter.api.Test;

class SchoolEmailValidatorTest {

  private final SchoolEmailValidator validator = new SchoolEmailValidator();

  @Test
  void acceptsSchoolInEnumWithMatchingEmailDomain() {
    assertThatCode(() -> validator.validate("덕성여자대학교", "student@DUKSUNG.AC.KR"))
        .doesNotThrowAnyException();
  }

  @Test
  void rejectsSchoolNotInEnum() {
    assertErrorCode(
        () -> validator.validate("임의대학교", "student@example.ac.kr"),
        UserErrorCode.UNSUPPORTED_UNIVERSITY);
  }

  @Test
  void rejectsMismatchedSchoolEmailDomain() {
    assertErrorCode(
        () -> validator.validate("덕성여자대학교", "student@snu.ac.kr"),
        UserErrorCode.SCHOOL_EMAIL_DOMAIN_MISMATCH);
  }

  private void assertErrorCode(
      org.assertj.core.api.ThrowableAssert.ThrowingCallable callable,
      UserErrorCode expectedErrorCode) {
    assertThatExceptionOfType(UserException.class)
        .isThrownBy(callable)
        .satisfies(exception -> assertThat(exception.errorCode()).isEqualTo(expectedErrorCode));
  }
}
