package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserExistenceJpaRepository extends JpaRepository<UserReferenceJpaEntity, Long> {

  @Query("select u.nickname from UserReferenceJpaEntity u where u.userId = :userId")
  Optional<String> findNicknameById(@Param("userId") Long userId);

  List<UserReferenceJpaEntity> findByUserIdIn(Set<Long> userIds);
}
