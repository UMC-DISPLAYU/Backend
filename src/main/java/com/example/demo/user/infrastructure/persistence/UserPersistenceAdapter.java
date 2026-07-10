package com.example.demo.user.infrastructure.persistence;

import com.example.demo.user.domain.entity.User;
import com.example.demo.user.domain.enums.Provider;
import com.example.demo.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    public boolean existsByProviderAndProviderId(
            Provider provider,
            String providerId
    ) {
        return userJpaRepository.existsByProviderAndProviderId(
                provider,
                providerId
        );
    }

    @Override
    public Optional<User> findByProviderAndProviderId(
            Provider provider,
            String providerId
    ) {
        return userJpaRepository.findByProviderAndProviderId(
                provider,
                providerId
        );
    }
}