package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, Long> {

  boolean existsByNickname(String nickname);

  boolean existsByProviderAndProviderId(Provider provider, String providerId);

  Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

  boolean existsBySchoolEmail(String schoolEmail);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select user from User user where user.id = :userId")
  Optional<User> findByIdForUpdate(@Param("userId") Long userId);
}
