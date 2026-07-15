package com.example.demo.domain.user.validator;

import com.example.demo.domain.user.domain.enums.University;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import org.springframework.stereotype.Component;

@Component
public class SchoolEmailValidator {

    public void validate(String univName, String email) {

        if (email == null || email.isBlank()) {
            throw new UserException(
                    UserErrorCode.INVALID_EMAIL
            );
        }


        String domain = extractDomain(email);


        boolean isValid = false;

        for (University university : University.values()) {

            if (university.getSchoolName().equals(univName)
                    && university.getDomain().equalsIgnoreCase(domain)) {

                isValid = true;
                break;
            }
        }


        if (!isValid) {
            throw new UserException(
                    UserErrorCode.UNSUPPORTED_UNIVERSITY
            );
        }
    }


    private String extractDomain(String email) {

        int index = email.indexOf("@");

        if (index == -1) {
            throw new UserException(
                    UserErrorCode.INVALID_EMAIL
            );
        }

        return email.substring(index + 1);
    }
}