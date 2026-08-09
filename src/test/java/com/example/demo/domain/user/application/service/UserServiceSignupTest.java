package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.command.AgreementCommand;
import com.example.demo.domain.user.application.command.SignupCommand;
import com.example.demo.domain.user.application.mapper.UserAgreementMapper;
import com.example.demo.domain.user.application.mapper.UserMapper;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.entity.AgreementPolicy;
import com.example.demo.domain.user.domain.entity.UserAgreement;
import com.example.demo.domain.user.domain.enums.AgreementCode;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.AgreementRepository;
import com.example.demo.domain.user.domain.repository.RefreshTokenRepository;
import com.example.demo.domain.user.domain.repository.UserAgreementRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.global.security.TokenProvider;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UserServiceSignupTest {

  private final UserRepository userRepository = mock(UserRepository.class);
  private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
  private final AgreementRepository agreementRepository = mock(AgreementRepository.class);
  private final UserAgreementRepository userAgreementRepository =
      mock(UserAgreementRepository.class);
  private final TokenProvider tokenProvider = mock(TokenProvider.class);
  private final UserService userService =
      new UserService(
          new UserMapper(),
          new UserAgreementMapper(),
          userRepository,
          refreshTokenRepository,
          agreementRepository,
          userAgreementRepository,
          new AgreementPolicy(),
          tokenProvider);

  private final SocialUserInfo socialUserInfo =
      new SocialUserInfo(
          Provider.Google, "google-provider-id", "Google User", "google@example.com");
  private final Agreement terms = agreement(11L, AgreementCode.TERMS_OF_SERVICE, true, 1);
  private final Agreement privacy = agreement(12L, AgreementCode.PRIVACY_COLLECTION_USE, true, 2);
  private final Agreement location = agreement(7L, AgreementCode.LOCATION_BASED_SERVICE, false, 3);
  private final List<Agreement> signupAgreements = List.of(terms, privacy, location);
  private final User savedUser =
      User.builder()
          .id(10L)
          .provider(Provider.Google)
          .providerId("google-provider-id")
          .name("Google User")
          .nickname("maya01")
          .socialEmail("google@example.com")
          .build();

  @BeforeEach
  void setUp() {
    when(userRepository.findByProviderAndProviderId(Provider.Google, "google-provider-id"))
        .thenReturn(Optional.empty());
    when(userRepository.existsByNickname("maya01")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(agreementRepository.findAllSignupAgreements()).thenReturn(signupAgreements);
    when(tokenProvider.createAccessToken(savedUser)).thenReturn("access-token");
    when(tokenProvider.createRefreshToken(savedUser)).thenReturn("refresh-token");
  }

  @Test
  void signsUpWithRequiredAgreementsAndSavesOptionalLocationWhenRequested() {
    userService.signup(
        command(
            List.of(
                agreement(AgreementCode.TERMS_OF_SERVICE),
                agreement(AgreementCode.PRIVACY_COLLECTION_USE),
                agreement(AgreementCode.LOCATION_BASED_SERVICE))),
        socialUserInfo);

    assertSavedAgreementIds(11L, 12L, 7L);
  }

  @Test
  void signsUpWithoutOptionalLocationAgreement() {
    userService.signup(
        command(
            List.of(
                agreement(AgreementCode.TERMS_OF_SERVICE),
                agreement(AgreementCode.PRIVACY_COLLECTION_USE))),
        socialUserInfo);

    assertSavedAgreementIds(11L, 12L);
  }

  @Test
  void rejectsSignupWhenRequiredAgreementIsMissing() {
    assertUserError(
        () ->
            userService.signup(
                command(List.of(agreement(AgreementCode.TERMS_OF_SERVICE))), socialUserInfo),
        UserErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED);
    assertNothingSaved();
  }

  @Test
  void rejectsSignupWhenUserIsNotOver14() {
    SignupCommand command =
        new SignupCommand(
            Nickname.of("maya01"),
            List.of(
                agreement(AgreementCode.TERMS_OF_SERVICE),
                agreement(AgreementCode.PRIVACY_COLLECTION_USE)),
            false);

    assertUserError(
        () -> userService.signup(command, socialUserInfo),
        UserErrorCode.OVER_14_CONFIRMATION_REQUIRED);
    assertNothingSaved();
  }

  @Test
  void rejectsUnknownOrInactiveAgreementVersion() {
    SignupCommand command =
        command(
            List.of(
                agreement(AgreementCode.TERMS_OF_SERVICE),
                new AgreementCommand(AgreementCode.PRIVACY_COLLECTION_USE, "2.0")));

    assertUserError(
        () -> userService.signup(command, socialUserInfo), UserErrorCode.AGREEMENT_NOT_FOUND);
    assertNothingSaved();
  }

  @Test
  void rejectsDuplicatedCodeAndVersion() {
    SignupCommand command =
        command(
            List.of(
                agreement(AgreementCode.TERMS_OF_SERVICE),
                agreement(AgreementCode.TERMS_OF_SERVICE),
                agreement(AgreementCode.PRIVACY_COLLECTION_USE)));

    assertUserError(
        () -> userService.signup(command, socialUserInfo), UserErrorCode.DUPLICATE_AGREEMENT);
    assertNothingSaved();
  }

  @Test
  void rejectsInvalidSignupAgreementConfiguration() {
    when(agreementRepository.findAllSignupAgreements()).thenReturn(List.of(terms, privacy));

    assertUserError(
        () ->
            userService.signup(
                command(
                    List.of(
                        agreement(AgreementCode.TERMS_OF_SERVICE),
                        agreement(AgreementCode.PRIVACY_COLLECTION_USE))),
                socialUserInfo),
        UserErrorCode.REQUIRED_AGREEMENT_NOT_FOUND);
    assertNothingSaved();
  }

  @Test
  void rejectsUnsupportedAgreementCodeAsInvalidConfiguration() {
    Agreement unsupported =
        Agreement.builder()
            .id(99L)
            .code("UNSUPPORTED_AGREEMENT")
            .version("1.0")
            .isActive(true)
            .isRequired(false)
            .displayOrder(3)
            .build();
    when(agreementRepository.findAllSignupAgreements())
        .thenReturn(List.of(terms, privacy, unsupported));

    assertUserError(
        () ->
            userService.signup(
                command(
                    List.of(
                        agreement(AgreementCode.TERMS_OF_SERVICE),
                        agreement(AgreementCode.PRIVACY_COLLECTION_USE))),
                socialUserInfo),
        UserErrorCode.REQUIRED_AGREEMENT_NOT_FOUND);
    assertNothingSaved();
  }

  @Test
  void rejectsDuplicatedActiveCodeAndVersionAsInvalidConfiguration() {
    Agreement duplicatedTerms =
        Agreement.builder()
            .id(98L)
            .code(AgreementCode.TERMS_OF_SERVICE.name())
            .version("1.0")
            .isActive(true)
            .isRequired(true)
            .displayOrder(4)
            .build();
    when(agreementRepository.findAllSignupAgreements())
        .thenReturn(List.of(terms, privacy, location, duplicatedTerms));

    assertUserError(
        () ->
            userService.signup(
                command(
                    List.of(
                        agreement(AgreementCode.TERMS_OF_SERVICE),
                        agreement(AgreementCode.PRIVACY_COLLECTION_USE))),
                socialUserInfo),
        UserErrorCode.REQUIRED_AGREEMENT_NOT_FOUND);
    assertNothingSaved();
  }

  private SignupCommand command(List<AgreementCommand> agreements) {
    return new SignupCommand(Nickname.of("maya01"), agreements, true);
  }

  private static AgreementCommand agreement(AgreementCode code) {
    return new AgreementCommand(code, "1.0");
  }

  private static Agreement agreement(
      Long id, AgreementCode code, boolean required, int displayOrder) {
    return Agreement.builder()
        .id(id)
        .code(code.name())
        .version("1.0")
        .isActive(true)
        .isRequired(required)
        .displayOrder(displayOrder)
        .build();
  }

  private void assertSavedAgreementIds(Long... ids) {
    ArgumentCaptor<List<UserAgreement>> agreementsCaptor = ArgumentCaptor.forClass(List.class);
    verify(userAgreementRepository).saveAll(agreementsCaptor.capture());
    assertThat(agreementsCaptor.getValue())
        .extracting(userAgreement -> userAgreement.getAgreement().getId())
        .containsExactlyInAnyOrder(ids);
  }

  private void assertNothingSaved() {
    verify(userRepository, never()).save(any(User.class));
    verify(userAgreementRepository, never()).saveAll(any());
  }

  private static void assertUserError(Runnable runnable, UserErrorCode errorCode) {
    assertThatThrownBy(runnable::run)
        .isInstanceOf(UserException.class)
        .extracting(exception -> ((UserException) exception).errorCode())
        .isEqualTo(errorCode);
  }
}
