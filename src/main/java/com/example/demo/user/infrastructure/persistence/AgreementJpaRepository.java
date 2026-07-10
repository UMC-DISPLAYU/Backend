package com.example.demo.user.infrastructure.persistence;

import com.example.demo.user.domain.entity.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgreementJpaRepository extends JpaRepository<Agreement, Long> {

    List<Agreement> findByIsRequiredTrue();

}
