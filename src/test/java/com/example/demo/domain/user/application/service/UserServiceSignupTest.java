package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.command.AgreementCommand;
import com.example.demo.domain.user.application.command.SignupCommand;
import com.example.demo.domain.user.application.mapper.UserAgreementMapper;
import com.example.demo.domain.user.application.mapper.UserMapper;
import com.example.demo.domain.user.application.result.SignupResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.entity.AgreementPolicy;
import com.example.demo.domain.user.domain.entity.RefreshToken;
import com.example.demo.domain.user.domain.entity.UserAgreement;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.domain.repository.AgreementRepository;
import com.example.demo.domain.user.domain.repository.RefreshTokenRepository;
import com.example.demo.domain.user.domain.repository.UserAgreementRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.global.security.TokenProvider;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UserServiceSignupTest {

  private final UserMapper userMapper = new UserMapper();
  private final UserAgreementMapper userAgreementMapper = new UserAgreementMapper();
  private final UserRepository userRepository = mock(UserRepository.class);
  private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
  private final AgreementRepository agreementRepository = mock(AgreementRepository.class);
  private final UserAgreementRepository userAgreementRepository =
      mock(UserAgreementRepository.class);
  private final TokenProvider tokenProvider = mock(TokenProvider.class);
  private final UserService userService =
      new UserService(
          userMapper,
          userAgreementMapper,
          userRepository,
          refreshTokenRepository,
          agreementRepository,
          userAgreementRepository,
          new AgreementPolicy(),
          tokenProvider);

  @Test
  void savesUserAgreementsAndTokensForOAuthSignup() {
    SocialUserInfo socialUserInfo =
        new SocialUserInfo(Provider.Google, "google-provider-id", "구글 사용자", "google@example.com");
    SignupCommand command =
        new SignupCommand(
            Nickname.of("maya"),
            List.of(
                new AgreementCommand(1L, true),
                new AgreementCommand(2L, true),
                new AgreementCommand(3L, false)));
    Agreement serviceAgreement =
        Agreement.builder().id(1L).title("서비스 이용약관").isRequired(true).build();
    Agreement privacyAgreement =
        Agreement.builder().id(2L).title("개인정보 처리방침").isRequired(true).build();
    User savedUser =
        User.builder()
            .id(10L)
            .provider(Provider.Google)
            .providerId("google-provider-id")
            .name("구글 사용자")
            .nickname("maya")
            .socialEmail("google@example.com")
            .build();
    when(userRepository.findByProviderAndProviderId(Provider.Google, "google-provider-id"))
        .thenReturn(Optional.empty());
    when(userRepository.existsByNickname("maya")).thenReturn(false);
    when(agreementRepository.findAllByIsRequiredTrue())
        .thenReturn(List.of(serviceAgreement, privacyAgreement));
    when(agreementRepository.findById(1L)).thenReturn(Optional.of(serviceAgreement));
    when(agreementRepository.findById(2L)).thenReturn(Optional.of(privacyAgreement));
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(tokenProvider.createAccessToken(savedUser)).thenReturn("access-token");
    when(tokenProvider.createRefreshToken(savedUser)).thenReturn("refresh-token");

    SignupResult result = userService.signup(command, socialUserInfo);

    assertThat(result.user()).isEqualTo(savedUser);
    assertThat(result.accessToken()).isEqualTo("access-token");
    assertThat(result.refreshToken()).isEqualTo("refresh-token");
    ArgumentCaptor<List<UserAgreement>> agreementsCaptor = ArgumentCaptor.forClass(List.class);
    verify(userAgreementRepository).saveAll(agreementsCaptor.capture());
    assertThat(agreementsCaptor.getValue()).hasSize(2);
    ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
    verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
    assertThat(refreshTokenCaptor.getValue().getRefreshToken()).isEqualTo("refresh-token");
  }
}
