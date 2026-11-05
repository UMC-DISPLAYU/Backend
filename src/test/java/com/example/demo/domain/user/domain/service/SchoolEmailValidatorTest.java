package com.example.demo.domain.user.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.domain.aggregate.University;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UniversityRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchoolEmailValidatorTest {

  private final UniversityRepository universityRepository = mock(UniversityRepository.class);
  private final SchoolEmailValidator validator = new SchoolEmailValidator(universityRepository);
  private final University university = mock(University.class);

  @BeforeEach
  void setUp() {
    when(university.getId()).thenReturn(1L);
    when(universityRepository.findByName("덕성여자대학교")).thenReturn(Optional.of(university));
  }

  @Test
  void acceptsSchoolInEnumWithMatchingEmailDomain() {
    when(universityRepository.existsEmailDomain(1L, "duksung.ac.kr")).thenReturn(true);

    assertThatCode(() -> validator.validate("덕성여자대학교", "student@DUKSUNG.AC.KR"))
        .doesNotThrowAnyException();
  }

  @Test
  void acceptsMultipleEmailDomainsForOneSchool() {
    when(universityRepository.findByName("국립경국대학교")).thenReturn(Optional.of(university));
    when(universityRepository.findByName("안동대학교")).thenReturn(Optional.of(university));
    when(universityRepository.existsEmailDomain(1L, "student.gknu.ac.kr")).thenReturn(true);
    when(universityRepository.existsEmailDomain(1L, "student.anu.ac.kr")).thenReturn(true);

    assertThatCode(() -> validator.validate("국립경국대학교", "student@student.gknu.ac.kr"))
        .doesNotThrowAnyException();
    assertThatCode(() -> validator.validate("안동대학교", "student@student.anu.ac.kr"))
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
    when(universityRepository.existsEmailDomain(1L, "snu.ac.kr")).thenReturn(false);

    assertErrorCode(
        () -> validator.validate("덕성여자대학교", "student@snu.ac.kr"),
        UserErrorCode.SCHOOL_EMAIL_DOMAIN_MISMATCH);
  }

  @Test
  void rejectsEmailWithMultipleAtSigns() {
    assertErrorCode(
        () -> validator.validate("덕성여자대학교", "student@invalid@duksung.ac.kr"),
        UserErrorCode.INVALID_EMAIL);
  }

  private void assertErrorCode(
      org.assertj.core.api.ThrowableAssert.ThrowingCallable callable,
      UserErrorCode expectedErrorCode) {
    assertThatExceptionOfType(UserException.class)
        .isThrownBy(callable)
        .satisfies(exception -> assertThat(exception.errorCode()).isEqualTo(expectedErrorCode));
  }
}
