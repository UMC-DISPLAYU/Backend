package com.example.demo.domain.user.domain.repository;



import java.util.Optional;

public interface SchoolEmailVerificationRepository {

    void save(SchoolEmailVerification verification);

    Optional<SchoolEmailVerification> findBySchoolEmail(String schoolEmail);
}