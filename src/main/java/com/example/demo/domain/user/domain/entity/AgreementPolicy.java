package com.example.demo.domain.user.domain.entity;

import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AgreementPolicy {

  private static final int SIGNUP_AGREEMENT_COUNT = 4;
  private static final int REQUIRED_SIGNUP_AGREEMENT_COUNT = 3;

  public void validateSignupConfiguration(List<Agreement> signupAgreements) {
    long requiredAgreementCount = signupAgreements.stream().filter(Agreement::isRequired).count();

    if (signupAgreements.size() != SIGNUP_AGREEMENT_COUNT
        || requiredAgreementCount != REQUIRED_SIGNUP_AGREEMENT_COUNT) {
      throw new UserException(UserErrorCode.REQUIRED_AGREEMENT_NOT_FOUND);
    }
  }

  public void validate(List<Agreement> requiredAgreements, Set<Long> agreedIds) {

    if (requiredAgreements.isEmpty()) {
      throw new UserException(UserErrorCode.REQUIRED_AGREEMENT_NOT_FOUND);
    }

    boolean allAccepted =
        requiredAgreements.stream().allMatch(agreement -> agreedIds.contains(agreement.getId()));

    if (!allAccepted) {
      throw new UserException(UserErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED);
    }
  }
}
