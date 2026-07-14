package com.example.demo.user.application.service;

import com.example.demo.user.application.auth.SocialUserInfo;
import com.example.demo.user.application.command.AgreementCommand;
import com.example.demo.user.application.command.SignupCommand;
import com.example.demo.user.application.mapper.UserAgreementMapper;
import com.example.demo.user.application.mapper.UserMapper;
import com.example.demo.user.application.result.SignupResult;
import com.example.demo.user.domain.entity.Agreement;
import com.example.demo.user.domain.entity.User;
import com.example.demo.user.domain.entity.UserAgreement;
import com.example.demo.user.domain.repository.AgreementRepository;
import com.example.demo.user.domain.repository.UserAgreementRepository;
import com.example.demo.user.domain.repository.UserRepository;
import com.example.demo.user.exception.UserErrorCode;
import com.example.demo.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final UserAgreementMapper userAgreementMapper;

    private final UserRepository userRepository;
    private final AgreementRepository agreementRepository;
    private final UserAgreementRepository userAgreementRepository;


    public SignupResult signup(
            SignupCommand command,
            SocialUserInfo socialUserInfo
    ) {

        validateNickname(command.nickname());

        validateRequiredAgreements(command.agreements());

        User user = userMapper.toUser(
                command,
                socialUserInfo
        );

        User savedUser = userRepository.save(user);

        saveUserAgreements(
                savedUser,
                command.agreements()
        );


        return new SignupResult(
                savedUser,
                null,
                null
        );
    }


    private void validateNickname(String nickname) {

        if (userRepository.existsByNickname(nickname)) {
            throw new UserException(
                    UserErrorCode.DUPLICATE_NICKNAME
            );
        }
    }


    private void validateRequiredAgreements(
            List<AgreementCommand> agreements
    ) {

        List<Agreement> requiredAgreements =
                agreementRepository.findRequiredAgreements();


        boolean isAllAccepted =
                requiredAgreements.stream()
                        .allMatch(required ->
                                agreements.stream()
                                        .anyMatch(request ->
                                                request.agreeId()
                                                        .equals(required.getId())
                                                        && request.isAgreed()
                                        )
                        );


        if (!isAllAccepted) {
            throw new UserException(
                    UserErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED
            );
        }
    }


    private void saveUserAgreements(
            User user,
            List<AgreementCommand> agreements
    ) {

        List<UserAgreement> userAgreements;
        userAgreements = agreements.stream()
                .map(command -> {

                    Agreement agreement =
                            agreementRepository.findById(
                                            command.agreeId()
                                    )
                                    .orElseThrow();

                    return userAgreementMapper.toUserAgreement(
                            user,
                            agreement,
                            command
                    );
                })
                .toList();


        userAgreementRepository.saveAll(userAgreements);
    }
}