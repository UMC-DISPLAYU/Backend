package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.type.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

  private final UserJpaRepository userJpaRepository;
  private final EntityManager entityManager;

  @Override
  public User save(User user) {
    return userJpaRepository.save(user);
  }

  @Override
  public void flush() {
    userJpaRepository.flush();
  }

  @Override
  public boolean existsByNickname(String nickname) {
    return userJpaRepository.existsByNickname(nickname);
  }

  @Override
  public boolean existsBySchoolEmail(String schoolEmail) {
    return userJpaRepository.existsBySchoolEmail(schoolEmail);
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

  @Override
  public Optional<User> findByIdForUpdate(Long userId) {
    Optional<User> user = userJpaRepository.findByIdForUpdate(userId);
    user.ifPresent(entity -> entityManager.refresh(entity, LockModeType.PESSIMISTIC_WRITE));
    return user;
  }

  @Override
  public List<User> findAllById(Collection<Long> userIds) {
    return userJpaRepository.findAllById(userIds);
  }
}
