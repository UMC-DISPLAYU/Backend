package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.RefreshToken;
import com.example.demo.domain.user.domain.repository.RefreshTokenRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepository {

  private final RefreshTokenJpaRepository refreshTokenJpaRepository;

  @Override
  public Optional<RefreshToken> findByUserId(Long userId) {

    return refreshTokenJpaRepository.findByUserId(userId);
  }

  @Override
  public RefreshToken save(RefreshToken refreshToken) {

    return refreshTokenJpaRepository.save(refreshToken);
  }

  @Override
  public void delete(RefreshToken refreshToken) {

    refreshTokenJpaRepository.delete(refreshToken);
  }
}
