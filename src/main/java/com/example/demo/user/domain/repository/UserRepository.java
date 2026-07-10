package com.example.demo.user.domain.repository;

import com.example.demo.user.domain.entity.User;
import com.example.demo.user.domain.enums.Provider;
import java.util.Optional;

public interface UserRepository {

  boolean existsByNickname(String nickname);

  boolean existsByProviderAndProviderId(Provider provider, String providerId);

  Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

  User save(User user);
}
