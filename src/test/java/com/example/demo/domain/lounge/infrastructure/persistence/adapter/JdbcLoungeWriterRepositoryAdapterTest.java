package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.lounge.domain.repository.LoungeWriterRepository;
import com.example.demo.domain.lounge.domain.vo.LoungeWriter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@JdbcTest
@ActiveProfiles("test")
@Import(JdbcLoungeWriterRepositoryAdapter.class)
class JdbcLoungeWriterRepositoryAdapterTest {

  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private LoungeWriterRepository writerRepository;

  @BeforeEach
  void setUp() {
    jdbcTemplate.execute(
        """
        CREATE TABLE `User` (
          userId BIGINT PRIMARY KEY,
          nickname VARCHAR(255),
          profileImageUrl VARCHAR(2048)
        )
        """);
  }

  @Test
  void findByUserIdsReturnsProfileImageUrl() {
    Long userId = 1L;
    String profileImageUrl = "https://cdn.example.com/profile.jpg";
    jdbcTemplate.update(
        "INSERT INTO `User` (userId, nickname, profileImageUrl) VALUES (?, ?, ?)",
        userId,
        "작성자",
        profileImageUrl);

    LoungeWriter writer = writerRepository.findByUserIds(List.of(userId)).get(userId);

    assertThat(writer).isEqualTo(new LoungeWriter(userId, "작성자", profileImageUrl));
  }
}
