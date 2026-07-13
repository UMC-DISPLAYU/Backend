package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

  private final UserJpaRepository userJpaRepository;

  @Override
  public User save(User user) {
    return userJpaRepository.save(user);
  }

  @Override
  public boolean existsByNickname(String nickname) {
    return userJpaRepository.existsByNickname(nickname);
  }

  @Override
  public boolean existsByProviderAndProviderId(Provider provider, String providerId) {
    return userJpaRepository.existsByProviderAndProviderId(provider, providerId);
  }

  @Override
  public Optional<User> findByProviderAndProviderId(Provider provider, String providerId) {
    return userJpaRepository.findByProviderAndProviderId(provider, providerId);
  }

  @Override
  public Optional<User> findById(Long userId) {
    return userJpaRepository.findById(userId);
  }
}
