package com.example.demo.domain.user.domain.entity;

import com.example.demo.domain.user.domain.enums.AgreementCode;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AgreementPolicy {

  private static final Set<AgreementCode> SIGNUP_AGREEMENT_CODES =
      Set.of(
          AgreementCode.TERMS_OF_SERVICE,
          AgreementCode.PRIVACY_COLLECTION_USE,
          AgreementCode.LOCATION_BASED_SERVICE);
  private static final Set<AgreementCode> REQUIRED_AGREEMENT_CODES =
      Set.of(AgreementCode.TERMS_OF_SERVICE, AgreementCode.PRIVACY_COLLECTION_USE);

  public void validateSignupConfiguration(List<Agreement> signupAgreements) {
    Set<AgreementCode> configuredCodes =
        signupAgreements.stream()
            .map(Agreement::getCode)
            .map(this::toAgreementCode)
            .collect(java.util.stream.Collectors.toSet());
    Set<AgreementCode> requiredCodes =
        signupAgreements.stream()
            .filter(Agreement::isRequired)
            .map(Agreement::getCode)
            .map(this::toAgreementCode)
            .collect(java.util.stream.Collectors.toSet());

    if (signupAgreements.size() != SIGNUP_AGREEMENT_CODES.size()
        || !configuredCodes.equals(SIGNUP_AGREEMENT_CODES)
        || !requiredCodes.equals(REQUIRED_AGREEMENT_CODES)) {
      throw new UserException(UserErrorCode.REQUIRED_AGREEMENT_NOT_FOUND);
    }
  }

  public void validateRequiredAgreements(Set<AgreementCode> requestedCodes) {
    if (!requestedCodes.containsAll(REQUIRED_AGREEMENT_CODES)) {
      throw new UserException(UserErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED);
    }
  }

  public void validateOver14(boolean isOver14) {
    if (!isOver14) {
      throw new UserException(UserErrorCode.OVER_14_CONFIRMATION_REQUIRED);
    }
  }

  public AgreementCode toAgreementCode(String code) {
    try {
      return AgreementCode.valueOf(code);
    } catch (IllegalArgumentException | NullPointerException exception) {
      throw new UserException(UserErrorCode.REQUIRED_AGREEMENT_NOT_FOUND);
    }
  }
}
