package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.domain.repository.LoungeWriterRepository;
import com.example.demo.domain.lounge.domain.vo.LoungeWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLoungeWriterRepositoryAdapter implements LoungeWriterRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public JdbcLoungeWriterRepositoryAdapter(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Map<Long, LoungeWriter> findByUserIds(List<Long> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }

    return jdbcTemplate
        .query(
            """
            SELECT userId, nickname
            FROM `User`
            WHERE userId IN (:userIds)
            """,
            new MapSqlParameterSource("userIds", userIds),
            (rs, rowNum) -> new LoungeWriter(rs.getLong("userId"), rs.getString("nickname"), null))
        .stream()
        .collect(Collectors.toMap(LoungeWriter::userId, writer -> writer));
  }
}
