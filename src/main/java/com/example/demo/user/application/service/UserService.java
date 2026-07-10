package com.example.demo.user.application.service;

import com.example.demo.user.application.auth.SocialUserInfo;
import com.example.demo.user.application.command.AgreementCommand;
import com.example.demo.user.application.command.SignupCommand;
import com.example.demo.user.application.mapper.UserAgreementMapper;
import com.example.demo.user.application.mapper.UserMapper;
import com.example.demo.user.application.result.SignupResult;
import com.example.demo.user.domain.entity.Agreement;
import com.example.demo.user.domain.entity.AgreementPolicy;
import com.example.demo.user.domain.entity.User;
import com.example.demo.user.domain.entity.UserAgreement;
import com.example.demo.user.domain.repository.AgreementRepository;
import com.example.demo.user.domain.repository.UserAgreementRepository;
import com.example.demo.user.domain.repository.UserRepository;
import com.example.demo.user.exception.UserErrorCode;
import com.example.demo.user.exception.UserException;
import java.util.List;
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
    private final AgreementRepository agreementRepository;
    private final UserAgreementRepository userAgreementRepository;

    private final AgreementPolicy agreementPolicy;

    public SignupResult signup(SignupCommand command, SocialUserInfo socialUserInfo) {

        Optional<User> existingUser =
                userRepository.findByProviderAndProviderId(
                        socialUserInfo.provider(), socialUserInfo.providerId());

        if (existingUser.isPresent()) {
            throw new UserException(UserErrorCode.ALREADY_REGISTERED_USER);
        }

        validateNickname(command.nickname());

        List<Agreement> requiredAgreements = agreementRepository.findAllByIsRequiredTrue();

        Set<Long> agreedIds =
                command.agreements().stream()
                        .filter(AgreementCommand::isAgreed)
                        .map(AgreementCommand::agreeId)
                        .collect(Collectors.toSet());

        agreementPolicy.validate(requiredAgreements, agreedIds);

        User user = userMapper.toUser(command, socialUserInfo);

        User savedUser = userRepository.save(user);

        saveUserAgreements(savedUser, command.agreements());

        return new SignupResult(savedUser, null, null);
    }

    private void validateNickname(String nickname) {

        if (userRepository.existsByNickname(nickname)) {
            throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void saveUserAgreements(User user, List<AgreementCommand> agreements) {

        List<UserAgreement> userAgreements =
                agreements.stream()
                        .map(
                                command -> {
                                    Agreement agreement =
                                            agreementRepository.findById(command.agreeId()).orElseThrow();

                                    return userAgreementMapper.toUserAgreement(user, agreement, command);
                                })
                        .toList();

        userAgreementRepository.saveAll(userAgreements);
    }
}