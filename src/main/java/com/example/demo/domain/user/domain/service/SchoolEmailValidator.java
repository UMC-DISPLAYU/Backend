package com.example.demo.domain.user.domain.service;

import com.example.demo.domain.user.domain.aggregate.University;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UniversityRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchoolEmailValidator {

  private final UniversityRepository universityRepository;

  public University validate(String univName, String email) {

    if (email == null || email.isBlank()) {
      throw new UserException(UserErrorCode.INVALID_EMAIL);
    }

    String domain = extractDomain(email).toLowerCase(Locale.ROOT);

    University university =
        universityRepository
            .findByName(univName)
            .orElseThrow(() -> new UserException(UserErrorCode.UNSUPPORTED_UNIVERSITY));

    if (!universityRepository.existsEmailDomain(university.getId(), domain)) {
      throw new UserException(UserErrorCode.SCHOOL_EMAIL_DOMAIN_MISMATCH);
    }

    return university;
  }

  private String extractDomain(String email) {

    int index = email.indexOf("@");

    if (index <= 0 || index != email.lastIndexOf("@") || index == email.length() - 1) {
      throw new UserException(UserErrorCode.INVALID_EMAIL);
    }

    return email.substring(index + 1);
  }
}
