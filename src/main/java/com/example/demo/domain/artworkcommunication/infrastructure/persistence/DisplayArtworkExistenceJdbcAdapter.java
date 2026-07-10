package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DisplayArtworkExistenceJdbcAdapter implements DisplayArtworkExistenceRepository {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public boolean existsById(Long displayArtworkId) {
    String sql = "SELECT COUNT(*) FROM DisplayArtwork WHERE displayArtworkId = ?";
    Long count = jdbcTemplate.queryForObject(sql, Long.class, displayArtworkId);
    return count != null && count > 0;
  }
}
