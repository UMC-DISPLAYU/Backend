package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResendSchoolEmailVerificationService {

    private final SendSchoolEmailVerificationService sendSchoolEmailVerificationService;

    public void execute(String schoolEmail) {

        sendSchoolEmailVerificationService.execute(
                new SendSchoolEmailVerificationCommand(
                        schoolEmail,
                        null
                )
        );
    }
}