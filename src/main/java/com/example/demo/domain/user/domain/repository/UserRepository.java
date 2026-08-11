package com.example.demo.domain.user.domain.repository;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

  boolean existsByNickname(String nickname);

  boolean existsByProviderAndProviderId(Provider provider, String providerId);

  Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

  Optional<User> findById(Long userId);

  List<User> findAllById(Collection<Long> userIds);

  boolean existsBySchoolEmail(String schoolEmail);

  User save(User user);

  void flush();
}
