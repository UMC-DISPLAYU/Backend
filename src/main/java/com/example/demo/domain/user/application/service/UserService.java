package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.command.AgreementCommand;
import com.example.demo.domain.user.application.command.SignupCommand;
import com.example.demo.domain.user.application.mapper.UserAgreementMapper;
import com.example.demo.domain.user.application.mapper.UserMapper;
import com.example.demo.domain.user.application.result.SignupResult;
import com.example.demo.domain.user.domain.entity.*;
import com.example.demo.domain.user.domain.repository.AgreementRepository;
import com.example.demo.domain.user.domain.repository.RefreshTokenRepository;
import com.example.demo.domain.user.domain.repository.UserAgreementRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import com.example.demo.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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



    public SignupResult signup(
            SignupCommand command,
            SocialUserInfo socialUserInfo
    ) {


        Optional<User> existingUser =
                userRepository.findByProviderAndProviderId(
                        socialUserInfo.provider(),
                        socialUserInfo.providerId()
                );


        if (existingUser.isPresent()) {

            throw new UserException(
                    UserErrorCode.ALREADY_REGISTERED_USER
            );
        }


        validateNickname(
                command.nickname()
        );


        List<Agreement> requiredAgreements =
                agreementRepository.findAllByIsRequiredTrue();


        Set<Long> agreedIds =
                command.agreements()
                        .stream()
                        .filter(
                                AgreementCommand::isAgreed
                        )
                        .map(
                                AgreementCommand::agreeId
                        )
                        .collect(
                                Collectors.toSet()
                        );


        agreementPolicy.validate(
                requiredAgreements,
                agreedIds
        );


        User user =
                userMapper.toUser(
                        command,
                        socialUserInfo
                );


        User savedUser =
                userRepository.save(
                        user
                );


        saveUserAgreements(
                savedUser,
                command.agreements()
        );


        String accessToken =
                tokenProvider.createAccessToken(
                        savedUser
                );


        String refreshToken =
                tokenProvider.createRefreshToken(
                        savedUser
                );


        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(savedUser)
                        .refreshToken(refreshToken)
                        .build()
        );


        return new SignupResult(
                savedUser,
                accessToken,
                refreshToken
        );
    }



    private void validateNickname(Nickname nickname) {
        if (userRepository.existsByNickname(nickname.value())) {
            throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public boolean isNicknameAvailable(String rawNickname) {
        // 형식이 틀리면 여기서 INVALID_NICKNAME_FORMAT 예외 발생 (VO 자체 검증)
        Nickname nickname = Nickname.of(rawNickname);
        return !userRepository.existsByNickname(nickname.value());
    }

    private void saveUserAgreements(User user, List<AgreementCommand> agreements) {
        List<Long> agreeIds = agreements.stream().map(AgreementCommand::agreeId).toList();

        Map<Long, Agreement> agreementsById =
                agreementRepository.findAllById(agreeIds)
                        .stream()
                        .collect(Collectors.toMap(Agreement::getId, a -> a));

        List<UserAgreement> userAgreements =
                agreements.stream()
                        .map(command -> {
                            Agreement agreement = Optional.ofNullable(agreementsById.get(command.agreeId()))
                                    .orElseThrow(() -> new UserException(UserErrorCode.AGREEMENT_NOT_FOUND));
                            return userAgreementMapper.toUserAgreement(user, agreement, command);
                        })
                        .toList();

        userAgreementRepository.saveAll(userAgreements);
    }

}