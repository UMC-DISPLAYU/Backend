package com.example.demo.domain.user.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
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
    assertUnsupported(() -> validator.validate("임의대학교", "student@example.ac.kr"));
  }

  @Test
  void rejectsMismatchedSchoolEmailDomain() {
    assertUnsupported(() -> validator.validate("덕성여자대학교", "student@snu.ac.kr"));
  }

  private void assertUnsupported(org.assertj.core.api.ThrowableAssert.ThrowingCallable callable) {
    assertThatExceptionOfType(UserException.class)
        .isThrownBy(callable)
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(UserErrorCode.UNSUPPORTED_UNIVERSITY));
  }
}
