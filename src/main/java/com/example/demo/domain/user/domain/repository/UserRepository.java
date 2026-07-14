package com.example.demo.domain.user.domain.repository;

import com.example.demo.domain.user.domain.entity.User;
import com.example.demo.domain.user.domain.enums.Provider;
import java.util.Optional;

public interface UserRepository {

  boolean existsByNickname(String nickname);

  boolean existsByProviderAndProviderId(Provider provider, String providerId);

  Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

  Optional<User> findById(Long userId);

  User save(User user);
}
