package com.example.demo.domain.artworkcommunication.domain.repository;

public interface UserExistenceRepository {
  boolean existsById(Long userId);
}
