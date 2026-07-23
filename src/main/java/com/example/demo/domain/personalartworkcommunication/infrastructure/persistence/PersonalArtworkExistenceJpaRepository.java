package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkExistenceJpaRepository
    extends JpaRepository<PersonalArtworkReferenceJpaEntity, Long> {

  boolean existsByPersonalArtworkIdAndDeletedAtIsNull(Long personalArtworkId);

  boolean existsByPersonalArtworkIdAndUserIdAndDeletedAtIsNull(Long personalArtworkId, Long userId);

  @Query(
      "select artwork.userId from PersonalArtworkReferenceJpaEntity artwork "
          + "where artwork.personalArtworkId = :personalArtworkId "
          + "and artwork.deletedAt is null")
  Optional<Long> findOwnerUserIdById(@Param("personalArtworkId") Long personalArtworkId);
}
