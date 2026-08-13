package com.example.demo.domain.artworkcommunication.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.domain.displayartwork.application.usecase.GetArtworkSummariesUseCase;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArtworkFeelingValidatorTest {

  @Mock private GetArtworkSummariesUseCase getArtworkSummariesUseCase;
  @Mock private UserExistenceRepository userExistenceRepository;
  @Mock private ArtworkFeelingReplyRepository artworkFeelingReplyRepository;

  private ArtworkFeelingValidator validator;

  @BeforeEach
  void setUp() {
    validator =
        new ArtworkFeelingValidator(
            getArtworkSummariesUseCase, userExistenceRepository, artworkFeelingReplyRepository);
  }

  @Test
  void validateDisplayArtworkExistsThrowsArtworkNotFoundWhenArtworkSummaryIsEmpty() {
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(1L))).thenReturn(List.of());

    assertThatThrownBy(() -> validator.validateDisplayArtworkExists(1L))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArtworkCommunicationErrorCode.ARTWORK_NOT_FOUND));
  }

  @Test
  void deletedFeelingCanRemainAReplyTarget() {
    ArtworkFeeling deletedFeeling = ArtworkFeeling.create(1L, 2L, "삭제된 감상평", List.of());
    deletedFeeling.delete();

    assertThatCode(() -> validator.validateReplyTarget(deletedFeeling, 1L))
        .doesNotThrowAnyException();
  }

  @Test
  void deletedFeelingCannotRemainAFeelingTarget() {
    ArtworkFeeling deletedFeeling = ArtworkFeeling.create(1L, 2L, "삭제된 감상평", List.of());
    deletedFeeling.delete();

    assertThatThrownBy(() -> validator.validateFeelingTarget(deletedFeeling, 1L))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArtworkCommunicationErrorCode.FEELING_NOT_FOUND));
  }
}
