package com.example.demo.user.infrastructure.persistence;

import com.example.demo.user.domain.entity.User;
import com.example.demo.user.domain.repository.UserRepository;
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
}
