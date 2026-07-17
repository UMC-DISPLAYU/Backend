package com.example.demo.domain.user.domain.repository;

import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import java.util.Optional;

public interface SchoolEmailVerificationRepository {

  SchoolEmailVerification save(SchoolEmailVerification verification);

  Optional<SchoolEmailVerification> findByUserIdAndSchoolEmail(Long userId, String schoolEmail);

  void deleteByUserIdAndSchoolEmail(Long userId, String schoolEmail);
}
