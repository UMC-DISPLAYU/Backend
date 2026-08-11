package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.global.config.JpaAuditingConfig;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class PersonalArtworkFeelingJpaRepositoryTest {

  @Autowired private PersonalArtworkFeelingJpaRepository personalArtworkFeelingJpaRepository;

  @Autowired
  private PersonalArtworkFeelingReplyJpaRepository personalArtworkFeelingReplyJpaRepository;

  @Test
  void activeFeelingIsReturnedWithoutActiveReplies() {
    PersonalArtworkFeeling activeFeeling =
        personalArtworkFeelingJpaRepository.saveAndFlush(
            PersonalArtworkFeeling.create(1L, 1L, "활성 감상평", List.of()));

    List<PersonalArtworkFeeling> feelings =
        personalArtworkFeelingJpaRepository.findByPersonalArtworkIdWithCursorIncludingDeleted(
            1L, null, PageRequest.of(0, 10));

    assertThat(feelings)
        .extracting(PersonalArtworkFeeling::getPersonalFeelingId)
        .containsExactly(activeFeeling.getPersonalFeelingId());
  }

  @Test
  void deletedFeelingIsReturnedOnlyWhileActiveReplyExists() {
    PersonalArtworkFeeling deletedFeeling =
        personalArtworkFeelingJpaRepository.saveAndFlush(
            PersonalArtworkFeeling.create(1L, 1L, "삭제할 감상평", List.of()));
    deletedFeeling.delete();
    personalArtworkFeelingJpaRepository.saveAndFlush(deletedFeeling);
    PersonalArtworkFeelingReply reply =
        personalArtworkFeelingReplyJpaRepository.saveAndFlush(
            PersonalArtworkFeelingReply.create(
                deletedFeeling.getPersonalFeelingId(), 2L, "활성 답변", List.of()));

    List<PersonalArtworkFeeling> feelingsWithActiveReply =
        personalArtworkFeelingJpaRepository.findByPersonalArtworkIdWithCursorIncludingDeleted(
            1L, null, PageRequest.of(0, 10));

    assertThat(feelingsWithActiveReply)
        .extracting(PersonalArtworkFeeling::getPersonalFeelingId)
        .containsExactly(deletedFeeling.getPersonalFeelingId());

    reply.delete();
    personalArtworkFeelingReplyJpaRepository.saveAndFlush(reply);

    List<PersonalArtworkFeeling> feelingsAfterLastReplyDeleted =
        personalArtworkFeelingJpaRepository.findByPersonalArtworkIdWithCursorIncludingDeleted(
            1L, null, PageRequest.of(0, 10));

    assertThat(feelingsAfterLastReplyDeleted).isEmpty();
  }
}
