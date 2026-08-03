package com.example.demo.domain.displayartwork.domain.repository;

import java.util.Optional;

public interface UserNicknameRepository {

  Optional<String> findNicknameById(Long userId);
}
