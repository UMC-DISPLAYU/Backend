package com.example.demo.user.domain.repository;

import com.example.demo.user.domain.entity.User;

public interface UserRepository {

    User save(User user);

    boolean existsByNickname(String nickname);
}
