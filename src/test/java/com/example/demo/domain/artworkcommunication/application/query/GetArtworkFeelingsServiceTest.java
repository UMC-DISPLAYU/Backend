package com.example.demo.domain.artworkcommunication.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
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
class GetArtworkFeelingsServiceTest {

  @Mock private GetArtworkSummariesUseCase getArtworkSummariesUseCase;
  @Mock private ArtworkFeelingRepository artworkFeelingRepository;
  @Mock private ArtworkFeelingReplyRepository artworkFeelingReplyRepository;
  @Mock private ArtworkFeelingLikeRepository artworkFeelingLikeRepository;
  @Mock private UserExistenceRepository userExistenceRepository;
  @Mock private CreatorExistenceRepository creatorExistenceRepository;
  @Mock private ArtworkFeelingUserDisplayResolver userDisplayResolver;

  private GetArtworkFeelingsService service;

  @BeforeEach
  void setUp() {
    service =
        new GetArtworkFeelingsService(
            getArtworkSummariesUseCase,
            artworkFeelingRepository,
            artworkFeelingReplyRepository,
            artworkFeelingLikeRepository,
            userExistenceRepository,
            creatorExistenceRepository,
            userDisplayResolver);
  }

  @Test
  void getFeelingsThrowsArtworkNotFoundWhenArtworkSummaryIsEmpty() {
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(1L))).thenReturn(List.of());

    assertThatThrownBy(() -> service.getFeelings(new GetArtworkFeelingsQuery(1L, null, 10, null)))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArtworkCommunicationErrorCode.ARTWORK_NOT_FOUND));
  }
}
