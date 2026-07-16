package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.command.VerifySchoolEmailVerificationCommand;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.repository.SchoolEmailVerificationRepository;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import com.example.demo.domain.user.infrastructure.mail.SchoolEmailSenderAdapter;
import com.example.demo.domain.user.validator.SchoolEmailValidator;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SchoolEmailVerificationServiceTest {

  private static final String SCHOOL_EMAIL = "student@school.ac.kr";
  private static final String UNIV_NAME = "대학교";
  private static final String VERIFICATION_CODE = "123456";

  @Test
  void invalidCodeIncreasesFailureCountAndInvalidatesVerificationOnFifthFailure() {
    SchoolEmailVerification verification = createVerification();
    FakeVerificationRepository repository = new FakeVerificationRepository(verification);
    VerifySchoolEmailVerificationService service =
        new VerifySchoolEmailVerificationService(repository);
    VerifySchoolEmailVerificationCommand command =
        new VerifySchoolEmailVerificationCommand(SCHOOL_EMAIL, "000000");

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
    VerifySchoolEmailVerificationService service =
        new VerifySchoolEmailVerificationService(repository);

    service.execute(new VerifySchoolEmailVerificationCommand(SCHOOL_EMAIL, VERIFICATION_CODE));

    assertThat(verification.isVerified()).isTrue();
    assertThat(verification.getFailedAttemptCount()).isZero();
  }

  @Test
  void sendAppliesCooldownWhenVerificationAlreadyExists() {
    FakeVerificationRepository repository = new FakeVerificationRepository(createVerification());
    SendSchoolEmailVerificationService service =
        new SendSchoolEmailVerificationService(
            repository, new FakeEmailSender(), new NoOpSchoolEmailValidator());

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(
            () -> service.execute(new SendSchoolEmailVerificationCommand(SCHOOL_EMAIL, UNIV_NAME)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(UserErrorCode.EMAIL_SEND_COOLDOWN));

    assertThat(repository.deleteCount).isZero();
    assertThat(repository.saveCount).isZero();
  }

  private SchoolEmailVerification createVerification() {
    return SchoolEmailVerification.create(SCHOOL_EMAIL, UNIV_NAME, VERIFICATION_CODE);
  }

  private static class FakeVerificationRepository implements SchoolEmailVerificationRepository {

    private SchoolEmailVerification verification;
    private int saveCount;
    private int deleteCount;

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
    public Optional<SchoolEmailVerification> findBySchoolEmail(String schoolEmail) {
      return Optional.ofNullable(verification);
    }

    @Override
    public void deleteBySchoolEmail(String schoolEmail) {
      verification = null;
      deleteCount++;
    }
  }

  private static class FakeEmailSender extends SchoolEmailSenderAdapter {

    private FakeEmailSender() {
      super(null);
    }

    @Override
    public void send(String schoolEmail, String verificationCode) {}
  }

  private static class NoOpSchoolEmailValidator extends SchoolEmailValidator {

    @Override
    public void validate(String univName, String email) {}
  }
}
