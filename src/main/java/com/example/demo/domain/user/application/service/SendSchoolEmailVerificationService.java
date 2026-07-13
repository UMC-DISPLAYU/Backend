package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.repository.SchoolEmailVerificationRepository;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import com.example.demo.domain.user.infrastructure.mail.SchoolEmailSenderAdapter;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SendSchoolEmailVerificationService {

    private final SchoolEmailVerificationRepository verificationRepository;
    private final SchoolEmailSenderAdapter emailSenderAdapter;

    @Transactional
    public void execute(
            SendSchoolEmailVerificationCommand command) {

        validateEmail(command.schoolEmail());

        verificationRepository.deleteBySchoolEmail(
                command.schoolEmail()
        );

        String verificationCode = createVerificationCode();

        SchoolEmailVerification verification =
                SchoolEmailVerification.create(
                        command.schoolEmail(),
                        verificationCode
                );

        verificationRepository.save(verification);

        emailSenderAdapter.send(
                command.schoolEmail(),
                verificationCode
        );
    }


    private void validateEmail(String email) {

        if (email == null
                || email.isBlank()
                || !email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.ac\\.kr$")) {

            throw new UserException(
                    UserErrorCode.INVALID_EMAIL);
        }
    }


    private String createVerificationCode() {

        return String.valueOf(
                new Random().nextInt(900000)
                        + 100000);
    }
}
