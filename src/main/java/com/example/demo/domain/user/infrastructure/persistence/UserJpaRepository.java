package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.enums.Provider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

  boolean existsByNickname(String nickname);

  boolean existsByProviderAndProviderId(Provider provider, String providerId);

  Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

  boolean existsBySchoolEmail(String schoolEmail);
}
