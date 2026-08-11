package com.example.demo.domain.user.domain.service;

import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.type.University;
import org.springframework.stereotype.Component;

@Component
public class SchoolEmailValidator {

  public void validate(String univName, String email) {

    if (email == null || email.isBlank()) {
      throw new UserException(UserErrorCode.INVALID_EMAIL);
    }

    String domain = extractDomain(email);

    University university =
        University.findBySchoolName(univName)
            .orElseThrow(() -> new UserException(UserErrorCode.UNSUPPORTED_UNIVERSITY));

    if (!university.getDomain().equalsIgnoreCase(domain)) {
      throw new UserException(UserErrorCode.SCHOOL_EMAIL_DOMAIN_MISMATCH);
    }
  }

  private String extractDomain(String email) {

    int index = email.indexOf("@");

    if (index == -1) {
      throw new UserException(UserErrorCode.INVALID_EMAIL);
    }

    return email.substring(index + 1);
  }
}
