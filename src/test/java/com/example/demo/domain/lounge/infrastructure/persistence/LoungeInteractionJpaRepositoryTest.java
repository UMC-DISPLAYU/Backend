package com.example.demo.domain.lounge.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:lounge_interaction_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
      "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl"
    })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class LoungeInteractionJpaRepositoryTest {

  @Autowired private SpringDataLoungePostLikeJpaRepository postLikeRepository;
  @Autowired private SpringDataLoungePostScrapJpaRepository postScrapRepository;
  @Autowired private SpringDataLoungeCommentLikeJpaRepository commentLikeRepository;

  @Test
  void postLikeInsertIsIdempotentAndSetsCreatedAt() {
    postLikeRepository.insertIfAbsent(1L, 2L);
    postLikeRepository.insertIfAbsent(1L, 2L);

    assertThat(postLikeRepository.findAll())
        .singleElement()
        .satisfies(postLike -> assertThat(postLike.getCreatedAt()).isNotNull());
  }

  @Test
  void postScrapInsertIsIdempotentAndSetsCreatedAt() {
    postScrapRepository.insertIfAbsent(1L, 2L);
    postScrapRepository.insertIfAbsent(1L, 2L);

    assertThat(postScrapRepository.findAll())
        .singleElement()
        .satisfies(postScrap -> assertThat(postScrap.getCreatedAt()).isNotNull());
  }

  @Test
  void commentLikeInsertIsIdempotentAndSetsCreatedAt() {
    commentLikeRepository.insertIfAbsent(1L, 2L);
    commentLikeRepository.insertIfAbsent(1L, 2L);

    assertThat(commentLikeRepository.findAll())
        .singleElement()
        .satisfies(commentLike -> assertThat(commentLike.getCreatedAt()).isNotNull());
  }

  @Test
  void insertDoesNotIgnoreIntegrityViolations() {
    assertThatThrownBy(() -> postLikeRepository.insertIfAbsent(null, 2L))
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}
