package com.example.demo.domain.user.domain.repository;

import com.example.demo.domain.user.domain.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByUserId(Long userId);

    RefreshToken save(
            RefreshToken refreshToken
    );

    void delete(RefreshToken refreshToken);
}