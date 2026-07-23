package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayCommunicationUserExistenceJpaRepository
    extends JpaRepository<DisplayCommunicationUserReferenceJpaEntity, Long> {
  boolean existsByUserIdAndDeletedAtIsNull(Long userId);
}
