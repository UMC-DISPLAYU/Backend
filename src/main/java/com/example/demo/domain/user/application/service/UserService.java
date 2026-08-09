package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.command.AgreementCommand;
import com.example.demo.domain.user.application.command.SignupCommand;
import com.example.demo.domain.user.application.mapper.UserAgreementMapper;
import com.example.demo.domain.user.application.mapper.UserMapper;
import com.example.demo.domain.user.application.result.SignupResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.entity.*;
import com.example.demo.domain.user.domain.enums.AgreementCode;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.AgreementRepository;
import com.example.demo.domain.user.domain.repository.RefreshTokenRepository;
import com.example.demo.domain.user.domain.repository.UserAgreementRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.global.security.TokenProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

  private final UserMapper userMapper;
  private final UserAgreementMapper userAgreementMapper;

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final AgreementRepository agreementRepository;
  private final UserAgreementRepository userAgreementRepository;

  private final AgreementPolicy agreementPolicy;

  private final TokenProvider tokenProvider;

  public SignupResult signup(SignupCommand command, SocialUserInfo socialUserInfo) {

    Optional<User> existingUser =
        userRepository.findByProviderAndProviderId(
            socialUserInfo.provider(), socialUserInfo.providerId());

    if (existingUser.isPresent()) {

      throw new UserException(UserErrorCode.ALREADY_REGISTERED_USER);
    }

    validateNickname(command.nickname());
    agreementPolicy.validateOver14(command.isOver14());

    List<AgreementCommand> agreementCommands =
        Optional.ofNullable(command.agreements()).orElseGet(List::of);

    if (agreementCommands.stream()
        .anyMatch(agreement -> agreement.code() == null || agreement.version() == null)) {
      throw new UserException(UserErrorCode.AGREEMENT_NOT_FOUND);
    }

    Set<AgreementKey> requestedAgreementKeys =
        agreementCommands.stream()
            .map(agreement -> new AgreementKey(agreement.code(), agreement.version()))
            .collect(Collectors.toSet());

    if (requestedAgreementKeys.size() != agreementCommands.size()) {
      throw new UserException(UserErrorCode.DUPLICATE_AGREEMENT);
    }

    List<Agreement> signupAgreements = agreementRepository.findAllSignupAgreements();
    agreementPolicy.validateSignupConfiguration(signupAgreements);

    Map<AgreementKey, Agreement> signupAgreementByKey = new HashMap<>();
    for (Agreement agreement : signupAgreements) {
      AgreementKey key =
          new AgreementKey(
              agreementPolicy.toAgreementCode(agreement.getCode()), agreement.getVersion());
      if (signupAgreementByKey.put(key, agreement) != null) {
        throw new UserException(UserErrorCode.REQUIRED_AGREEMENT_NOT_FOUND);
      }
    }

    if (!signupAgreementByKey.keySet().containsAll(requestedAgreementKeys)) {
      throw new UserException(UserErrorCode.AGREEMENT_NOT_FOUND);
    }

    Set<AgreementCode> requestedCodes =
        agreementCommands.stream().map(AgreementCommand::code).collect(Collectors.toSet());
    agreementPolicy.validateRequiredAgreements(requestedCodes);

    List<Agreement> requestedAgreements =
        requestedAgreementKeys.stream().map(signupAgreementByKey::get).toList();

    User user = userMapper.toUser(command, socialUserInfo);

    User savedUser = userRepository.save(user);

    saveUserAgreements(savedUser, requestedAgreements);

    String accessToken = tokenProvider.createAccessToken(savedUser);

    String refreshToken = tokenProvider.createRefreshToken(savedUser);

    refreshTokenRepository.save(
        RefreshToken.builder().user(savedUser).refreshToken(refreshToken).build());

    return new SignupResult(savedUser, accessToken, refreshToken);
  }

  private void validateNickname(Nickname nickname) {

    if (userRepository.existsByNickname(nickname.value())) {

      throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
    }
  }

  public boolean isNicknameAvailable(String rawNickname) {

    Nickname nickname = Nickname.of(rawNickname);

    return !userRepository.existsByNickname(nickname.value());
  }

  private void saveUserAgreements(User user, List<Agreement> requestedAgreements) {
    List<UserAgreement> userAgreements =
        requestedAgreements.stream()
            .map(agreement -> userAgreementMapper.toUserAgreement(user, agreement))
            .toList();

    userAgreementRepository.saveAll(userAgreements);
  }

  private record AgreementKey(AgreementCode code, String version) {}
}
