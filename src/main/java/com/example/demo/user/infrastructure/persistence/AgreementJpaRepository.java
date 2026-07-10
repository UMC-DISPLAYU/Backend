package com.example.demo.user.infrastructure.persistence;

import com.example.demo.user.domain.entity.Agreement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgreementJpaRepository extends JpaRepository<Agreement, Long> {

  List<Agreement> findByIsRequiredTrue();

  List<Agreement> findAllByIsRequiredTrue();
}
