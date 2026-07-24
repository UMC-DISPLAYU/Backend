package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayCommunicationUserExistenceJpaRepository
    extends JpaRepository<DisplayCommunicationUserReferenceJpaEntity, Long> {
  boolean existsByUserIdAndDeletedAtIsNull(Long userId);

  Optional<DisplayCommunicationUserReferenceJpaEntity> findByUserIdAndDeletedAtIsNull(Long userId);
}
