package com.example.demo.domain.displaycommunication.domain.repository;

public interface UserExistenceRepository {
  boolean existsById(Long userId);
}
