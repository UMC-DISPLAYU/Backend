package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenJpaRepository
        extends JpaRepository<RefreshToken, Long> {


    Optional<RefreshToken> findByUserId(
            Long userId
    );
}
