package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.Agreement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgreementJpaRepository extends JpaRepository<Agreement, Long> {

  @Query(
      """
      SELECT agreement
      FROM Agreement agreement
      WHERE agreement.code IN :codes
        AND agreement.isActive = true
        AND agreement.effectiveDate <= :asOfDate
        AND agreement.effectiveDate = (
          SELECT MAX(candidate.effectiveDate)
          FROM Agreement candidate
          WHERE candidate.code = agreement.code
            AND candidate.isActive = true
            AND candidate.effectiveDate <= :asOfDate
        )
      ORDER BY agreement.displayOrder ASC
      """)
  List<Agreement> findCurrentByCodes(
      @Param("codes") Collection<String> codes, @Param("asOfDate") LocalDate asOfDate);
}
