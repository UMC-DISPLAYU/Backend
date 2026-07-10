package com.example.demo.user.infrastructure.persistence;

import com.example.demo.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByNickname(String nickname);
}
