package com.example.demo.user.infrastructure.persistence;

import com.example.demo.user.domain.entity.User;
import com.example.demo.user.domain.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByNickname(String nickname);
    boolean existsByProviderAndProviderId(
            Provider provider,
            String providerId
    );
    Optional<User> findByProviderAndProviderId(
            Provider provider,
            String providerId
    );
}
