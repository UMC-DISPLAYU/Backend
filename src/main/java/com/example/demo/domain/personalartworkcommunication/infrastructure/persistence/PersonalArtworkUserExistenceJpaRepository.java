package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkUserExistenceJpaRepository
    extends JpaRepository<PersonalArtworkUserReferenceJpaEntity, Long> {

  @Query("select u.nickname from PersonalArtworkUserReferenceJpaEntity u where u.userId = :userId")
  Optional<String> findNicknameByUserId(@Param("userId") Long userId);

  List<PersonalArtworkUserReferenceJpaEntity> findByUserIdIn(Set<Long> userIds);
}
