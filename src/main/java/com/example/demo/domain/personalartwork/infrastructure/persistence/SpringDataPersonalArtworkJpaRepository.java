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

  // 삭제된 작품이 섞이면 다른 도메인의 존재 검증이 무력화되므로 쿼리에서 걸러낸다.
  @Query(
      """
      SELECT artwork
      FROM PersonalArtwork artwork
      WHERE artwork.id IN :personalArtworkIds
        AND artwork.deletedAt IS NULL
      ORDER BY artwork.createdAt ASC, artwork.id ASC
      """)
  List<PersonalArtwork> findAllByIdInAndDeletedAtIsNull(
      @Param("personalArtworkIds") List<Long> personalArtworkIds);
}
