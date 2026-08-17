package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.command.VerifySchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.port.SchoolEmailSenderPort;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.SchoolEmailVerificationRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.service.SchoolEmailValidator;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SchoolEmailVerificationServiceTest {

  private static final Long USER_ID = 1L;
  private static final String SCHOOL_EMAIL = "student@school.ac.kr";
  private static final String UNIV_NAME = "대학교";
  private static final String VERIFICATION_CODE = "123456";

  @Test
  void invalidCodeIncreasesFailureCountAndInvalidatesVerificationOnFifthFailure() {
    SchoolEmailVerification verification = createVerification();
    FakeVerificationRepository repository = new FakeVerificationRepository(verification);
    UserRepository userRepository = mock(UserRepository.class);
    VerifySchoolEmailVerificationService service =
        new VerifySchoolEmailVerificationService(repository, userRepository);
    VerifySchoolEmailVerificationCommand command =
        new VerifySchoolEmailVerificationCommand(USER_ID, SCHOOL_EMAIL, "000000");

    for (int attempt = 1; attempt < 5; attempt++) {
      assertThatExceptionOfType(UserException.class)
          .isThrownBy(() -> service.execute(command))
          .satisfies(
              exception ->
                  assertThat(exception.errorCode())
                      .isEqualTo(UserErrorCode.VERIFICATION_CODE_MISMATCH));
    }

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(() -> service.execute(command))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(UserErrorCode.VERIFICATION_ATTEMPTS_EXCEEDED));

    assertThat(verification.getFailedAttemptCount()).isEqualTo(5);
    assertThat(verification.hasExceededMaxAttempts()).isTrue();
    assertThat(verification.isExpired()).isTrue();
    assertThat(repository.saveCount).isEqualTo(5);
  }

  @Test
  void successfulVerificationCompletesVerificationAndResetsFailureCount() {
    SchoolEmailVerification verification = createVerification();
    verification.recordFailedAttempt();
    verification.recordFailedAttempt();
    FakeVerificationRepository repository = new FakeVerificationRepository(verification);
    UserRepository userRepository = mock(UserRepository.class);
    VerifySchoolEmailVerificationService service =
        new VerifySchoolEmailVerificationService(repository, userRepository);

    service.execute(
        new VerifySchoolEmailVerificationCommand(USER_ID, SCHOOL_EMAIL, VERIFICATION_CODE));

    assertThat(verification.isVerified()).isTrue();
    assertThat(verification.getFailedAttemptCount()).isZero();
    assertThat(verification.getUser().getSchoolEmail()).isEqualTo(SCHOOL_EMAIL);
    assertThat(verification.getUser().getUnivName()).isEqualTo(UNIV_NAME);
    assertThat(verification.getUser().isVerified()).isFalse();
  }

  @Test
  void sendAppliesCooldownWhenVerificationAlreadyExists() {
    FakeVerificationRepository repository = new FakeVerificationRepository(createVerification());
    UserRepository userRepository = mock(UserRepository.class);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(createUser()));
    SendSchoolEmailVerificationService service =
        new SendSchoolEmailVerificationService(
            repository, new FakeEmailSender(), new NoOpSchoolEmailValidator(), userRepository);

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(
            () ->
                service.execute(
                    new SendSchoolEmailVerificationCommand(USER_ID, SCHOOL_EMAIL, UNIV_NAME)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(UserErrorCode.EMAIL_SEND_COOLDOWN));

    assertThat(repository.deleteCount).isZero();
    assertThat(repository.saveCount).isZero();
  }

  @Test
  void sendRejectsSchoolEmailAlreadyOwnedByAnotherUser() {
    FakeVerificationRepository repository = new FakeVerificationRepository(null);
    UserRepository userRepository = mock(UserRepository.class);
    SchoolEmailSenderPort emailSender = mock(SchoolEmailSenderPort.class);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(createUser()));
    when(userRepository.existsBySchoolEmail(SCHOOL_EMAIL)).thenReturn(true);
    SendSchoolEmailVerificationService service =
        new SendSchoolEmailVerificationService(
            repository, emailSender, new NoOpSchoolEmailValidator(), userRepository);

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(
            () ->
                service.execute(
                    new SendSchoolEmailVerificationCommand(USER_ID, SCHOOL_EMAIL, UNIV_NAME)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(UserErrorCode.DUPLICATE_SCHOOL_EMAIL));

    verifyNoInteractions(emailSender);
    assertThat(repository.saveCount).isZero();
  }

  @Test
  void verificationRejectsSchoolEmailAlreadyOwnedByAnotherUser() {
    SchoolEmailVerification verification = createVerification();
    FakeVerificationRepository repository = new FakeVerificationRepository(verification);
    UserRepository userRepository = mock(UserRepository.class);
    when(userRepository.existsBySchoolEmail(SCHOOL_EMAIL)).thenReturn(true);
    VerifySchoolEmailVerificationService service =
        new VerifySchoolEmailVerificationService(repository, userRepository);

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(
            () ->
                service.execute(
                    new VerifySchoolEmailVerificationCommand(
                        USER_ID, SCHOOL_EMAIL, VERIFICATION_CODE)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(UserErrorCode.DUPLICATE_SCHOOL_EMAIL));

    assertThat(verification.isVerified()).isFalse();
    assertThat(verification.getUser().getSchoolEmail()).isNull();
  }

  @Test
  void userCannotReplacePreviouslyVerifiedSchoolEmail() {
    User user = createUser();
    user.verifySchoolEmail(SCHOOL_EMAIL, UNIV_NAME);

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(() -> user.verifySchoolEmail("other@school.ac.kr", UNIV_NAME))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(UserErrorCode.ALREADY_VERIFIED_USER));

    assertThat(user.getSchoolEmail()).isEqualTo(SCHOOL_EMAIL);
  }

  @Test
  void sendRejectsNullSchoolEmailAsInvalidEmail() {
    FakeVerificationRepository repository = new FakeVerificationRepository(null);
    UserRepository userRepository = mock(UserRepository.class);
    SendSchoolEmailVerificationService service =
        new SendSchoolEmailVerificationService(
            repository, new FakeEmailSender(), new NoOpSchoolEmailValidator(), userRepository);

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(
            () -> service.execute(new SendSchoolEmailVerificationCommand(USER_ID, null, UNIV_NAME)))
        .satisfies(
            exception -> assertThat(exception.errorCode()).isEqualTo(UserErrorCode.INVALID_EMAIL));
  }

  @Test
  void verificationRejectsBlankSchoolEmailAsInvalidEmail() {
    VerifySchoolEmailVerificationService service =
        new VerifySchoolEmailVerificationService(
            new FakeVerificationRepository(null), mock(UserRepository.class));

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(
            () ->
                service.execute(
                    new VerifySchoolEmailVerificationCommand(USER_ID, "  ", VERIFICATION_CODE)))
        .satisfies(
            exception -> assertThat(exception.errorCode()).isEqualTo(UserErrorCode.INVALID_EMAIL));
  }

  @Test
  void resendNormalizesSchoolEmailBeforeLookupAndSending() {
    FakeVerificationRepository repository = new FakeVerificationRepository(createVerification());
    SendSchoolEmailVerificationService sendService = mock(SendSchoolEmailVerificationService.class);
    ResendSchoolEmailVerificationService service =
        new ResendSchoolEmailVerificationService(repository, sendService);

    service.execute(USER_ID, "  STUDENT@SCHOOL.AC.KR  ");

    assertThat(repository.lastFindSchoolEmail).isEqualTo(SCHOOL_EMAIL);
    verify(sendService)
        .execute(new SendSchoolEmailVerificationCommand(USER_ID, SCHOOL_EMAIL, UNIV_NAME));
  }

  private SchoolEmailVerification createVerification() {
    return SchoolEmailVerification.create(createUser(), SCHOOL_EMAIL, UNIV_NAME, VERIFICATION_CODE);
  }

  private User createUser() {
    return User.builder().id(USER_ID).build();
  }

  private static class FakeVerificationRepository implements SchoolEmailVerificationRepository {

    private SchoolEmailVerification verification;
    private int saveCount;
    private int deleteCount;
    private String lastFindSchoolEmail;

    private FakeVerificationRepository(SchoolEmailVerification verification) {
      this.verification = verification;
    }

    @Override
    public SchoolEmailVerification save(SchoolEmailVerification verification) {
      this.verification = verification;
      saveCount++;
      return verification;
    }

    @Override
    public Optional<SchoolEmailVerification> findByUserIdAndSchoolEmail(
        Long userId, String schoolEmail) {
      lastFindSchoolEmail = schoolEmail;
      return Optional.ofNullable(verification);
    }

    @Override
    public void deleteByUserIdAndSchoolEmail(Long userId, String schoolEmail) {
      verification = null;
      deleteCount++;
    }
  }

  private static class FakeEmailSender implements SchoolEmailSenderPort {

    @Override
    public void send(String schoolEmail, String verificationCode) {}
  }

  private static class NoOpSchoolEmailValidator extends SchoolEmailValidator {

    @Override
    public void validate(String univName, String email) {}
  }
}
