package com.example.demo.domain.user.application.service;



import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;

import com.univcert.api.UnivCert;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SendSchoolEmailVerificationService {


    @Value("${univCert.key}")
    private String key;


    public void execute(
            SendSchoolEmailVerificationCommand command
    ) throws IOException {


        validateEmail(command.schoolEmail());


        // 학교명 검증
        Map<String, Object> check =
                UnivCert.check(
                        command.univName()
                );


        boolean universityExists =
                (boolean) check.get("success");


        if (!universityExists) {

            throw new UserException(
                    UserErrorCode.UNSUPPORTED_UNIVERSITY
            );
        }


        // 기존 인증 요청 제거
        UnivCert.clear(
                key,
                command.schoolEmail()
        );


        // 인증번호 발송
        Map<String, Object> result =
                UnivCert.certify(
                        key,
                        command.schoolEmail(),
                        command.univName(),
                        true
                );


        boolean success =
                (boolean) result.get("success");


        if (!success) {

            throw new UserException(
                    UserErrorCode.EMAIL_SEND_FAILED
            );
        }
    }


    private void validateEmail(String email) {

        if(email == null ||
                email.isBlank() ||
                !email.contains("@")) {

            throw new UserException(
                    UserErrorCode.INVALID_EMAIL
            );
        }
    }
}
