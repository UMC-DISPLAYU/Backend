package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkExistenceJpaRepository
    extends JpaRepository<PersonalArtworkReferenceJpaEntity, Long> {

  boolean existsByPersonalArtworkIdAndUserId(Long personalArtworkId, Long userId);

  @Query(
      "select artwork.userId from PersonalArtworkReferenceJpaEntity artwork "
          + "where artwork.personalArtworkId = :personalArtworkId")
  Optional<Long> findOwnerUserIdById(@Param("personalArtworkId") Long personalArtworkId);
}
