package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserExistenceJdbcAdapter implements UserExistenceRepository {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public boolean existsById(Long userId) {
    String sql = "SELECT COUNT(*) FROM `User` WHERE userId = ?";
    Long count = jdbcTemplate.queryForObject(sql, Long.class, userId);
    return count != null && count > 0;
  }
}
