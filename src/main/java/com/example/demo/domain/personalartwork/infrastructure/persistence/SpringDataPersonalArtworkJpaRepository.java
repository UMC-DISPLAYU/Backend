package com.example.demo.domain.personalartwork.infrastructure.persistence;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataPersonalArtworkJpaRepository
    extends JpaRepository<PersonalArtwork, Long> {

  @Query(
      """
      SELECT artwork
      FROM PersonalArtwork artwork
      WHERE artwork.ownerUserId.value = :userId
        AND artwork.deletedAt IS NULL
      ORDER BY artwork.createdAt ASC, artwork.id ASC
      """)
  List<PersonalArtwork> findAllByOwnerUserIdOrderByCreatedAtAsc(@Param("userId") Long userId);
}
