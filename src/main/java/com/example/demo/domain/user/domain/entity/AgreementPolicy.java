package com.example.demo.domain.user.domain.entity;

import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AgreementPolicy {

  public void validate(List<Agreement> requiredAgreements, Set<Long> agreedIds) {

    boolean allAccepted =
        requiredAgreements.stream().allMatch(agreement -> agreedIds.contains(agreement.getId()));

    if (!allAccepted) {
      throw new UserException(UserErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED);
    }
  }
}
