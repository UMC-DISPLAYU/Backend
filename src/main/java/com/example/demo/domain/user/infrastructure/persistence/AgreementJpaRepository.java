package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.Agreement;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgreementJpaRepository extends JpaRepository<Agreement, Long> {

  List<Agreement> findAllByOrderByIdAsc();

  List<Agreement> findAllByTitleInOrderByIdAsc(Collection<String> titles);

  List<Agreement> findAllByIsRequiredTrue();
}
