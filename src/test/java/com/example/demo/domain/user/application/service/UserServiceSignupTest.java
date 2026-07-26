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
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.domain.repository.AgreementRepository;
import com.example.demo.domain.user.domain.repository.RefreshTokenRepository;
import com.example.demo.domain.user.domain.repository.UserAgreementRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
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
  private final Agreement locationAgreement = agreement(70L, "위치 기반 서비스 약관", true);
  private final Agreement serviceAgreement = agreement(110L, "서비스 이용약관", true);
  private final Agreement privacyAgreement = agreement(120L, "개인정보 처리방침", true);
  private final Agreement marketingAgreement = agreement(130L, "마케팅 정보 수신 동의", false);
  private final List<Agreement> signupAgreements =
      List.of(locationAgreement, serviceAgreement, privacyAgreement, marketingAgreement);
  private final User savedUser =
      User.builder()
          .id(10L)
          .provider(Provider.Google)
          .providerId("google-provider-id")
          .name("Google User")
          .nickname("maya")
          .socialEmail("google@example.com")
          .build();

  @BeforeEach
  void setUp() {
    when(userRepository.findByProviderAndProviderId(Provider.Google, "google-provider-id"))
        .thenReturn(Optional.empty());
    when(userRepository.existsByNickname("maya")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(agreementRepository.findAllSignupAgreements()).thenReturn(signupAgreements);
    when(tokenProvider.createAccessToken(savedUser)).thenReturn("access-token");
    when(tokenProvider.createRefreshToken(savedUser)).thenReturn("refresh-token");
  }

  @Test
  void signsUpWithAllRequiredAgreementsUsingDatabaseIds() {
    SignupCommand command =
        command(
            List.of(agreed(70L), agreed(110L), agreed(120L), new AgreementCommand(130L, false)));
    when(agreementRepository.findAllById(any())).thenReturn(signupAgreements);

    userService.signup(command, socialUserInfo);

    assertSavedAgreementIds(70L, 110L, 120L);
  }

  @Test
  void rejectsSignupWhenLocationAgreementIsMissing() {
    SignupCommand command = command(List.of(agreed(110L), agreed(120L)));
    when(agreementRepository.findAllById(any()))
        .thenReturn(List.of(serviceAgreement, privacyAgreement));

    assertUserError(
        () -> userService.signup(command, socialUserInfo),
        UserErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED);
    assertNothingSaved();
  }

  @Test
  void rejectsSignupWhenServiceAgreementIsNotAgreed() {
    SignupCommand command =
        command(List.of(agreed(70L), new AgreementCommand(110L, false), agreed(120L)));
    when(agreementRepository.findAllById(any()))
        .thenReturn(List.of(locationAgreement, serviceAgreement, privacyAgreement));

    assertUserError(
        () -> userService.signup(command, socialUserInfo),
        UserErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED);
    assertNothingSaved();
  }

  @Test
  void rejectsSignupWhenPrivacyAgreementIsMissing() {
    SignupCommand command = command(List.of(agreed(70L), agreed(110L)));
    when(agreementRepository.findAllById(any()))
        .thenReturn(List.of(locationAgreement, serviceAgreement));

    assertUserError(
        () -> userService.signup(command, socialUserInfo),
        UserErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED);
    assertNothingSaved();
  }

  @Test
  void signsUpWhenMarketingAgreementIsNotAgreed() {
    SignupCommand command =
        command(
            List.of(agreed(70L), agreed(110L), agreed(120L), new AgreementCommand(130L, false)));
    when(agreementRepository.findAllById(any())).thenReturn(signupAgreements);

    userService.signup(command, socialUserInfo);

    assertSavedAgreementIds(70L, 110L, 120L);
  }

  @Test
  void ignoresPastRequiredAgreementsWhenValidatingSignup() {
    Agreement pastService = agreement(1L, "서비스 이용약관 v1", true);
    Agreement pastPrivacy = agreement(2L, "개인정보 처리방침 v1", true);
    Agreement previousService = agreement(4L, "서비스 이용약관 v2", true);
    Agreement previousPrivacy = agreement(5L, "개인정보 처리방침 v2", true);
    Agreement artworkGuide = agreement(8L, "작품 공개 정책 안내", true);
    Agreement archiveGuide = agreement(9L, "아카이브 이용 안내", true);
    SignupCommand command = command(List.of(agreed(70L), agreed(110L), agreed(120L)));
    when(agreementRepository.findAllById(any()))
        .thenReturn(List.of(locationAgreement, serviceAgreement, privacyAgreement));

    userService.signup(command, socialUserInfo);

    assertThat(
            List.of(
                pastService,
                pastPrivacy,
                previousService,
                previousPrivacy,
                artworkGuide,
                archiveGuide))
        .allMatch(Agreement::isRequired);
    assertSavedAgreementIds(70L, 110L, 120L);
  }

  @Test
  void rejectsSignupWhenUnknownAgreementIdIsIncluded() {
    SignupCommand command = command(List.of(agreed(70L), agreed(110L), agreed(120L), agreed(999L)));
    when(agreementRepository.findAllById(any()))
        .thenReturn(List.of(locationAgreement, serviceAgreement, privacyAgreement));

    assertUserError(
        () -> userService.signup(command, socialUserInfo), UserErrorCode.AGREEMENT_NOT_FOUND);
    assertNothingSaved();
  }

  @Test
  void savesEachAgreementOnceWhenIdsAreDuplicated() {
    SignupCommand command =
        command(
            List.of(
                agreed(70L), agreed(70L), agreed(110L), agreed(110L), agreed(120L), agreed(120L)));
    when(agreementRepository.findAllById(any()))
        .thenReturn(List.of(locationAgreement, serviceAgreement, privacyAgreement));

    userService.signup(command, socialUserInfo);

    assertSavedAgreementIds(70L, 110L, 120L);
  }

  @Test
  void rejectsSignupWhenAgreementListIsNullOrEmpty() {
    when(agreementRepository.findAllById(any())).thenReturn(List.of());

    assertUserError(
        () -> userService.signup(command(null), socialUserInfo),
        UserErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED);
    assertUserError(
        () -> userService.signup(command(List.of()), socialUserInfo),
        UserErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void rejectsSignupWhenSignupAgreementsAreNotConfigured() {
    when(agreementRepository.findAllById(any())).thenReturn(List.of());
    when(agreementRepository.findAllSignupAgreements()).thenReturn(List.of());

    assertUserError(
        () -> userService.signup(command(List.of()), socialUserInfo),
        UserErrorCode.REQUIRED_AGREEMENT_NOT_FOUND);
    verify(userRepository, never()).save(any(User.class));
  }

  private SignupCommand command(List<AgreementCommand> agreements) {
    return new SignupCommand(Nickname.of("maya"), agreements);
  }

  private static AgreementCommand agreed(Long id) {
    return new AgreementCommand(id, true);
  }

  private static Agreement agreement(Long id, String title, boolean required) {
    return Agreement.builder().id(id).title(title).isRequired(required).build();
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
