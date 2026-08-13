package com.example.demo.domain.personalartworkcommunication.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.demo.domain.personalartwork.application.usecase.GetPersonalArtworkAccessUseCase;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonalArtworkFeelingValidatorTest {

  @Mock private GetPersonalArtworkAccessUseCase getPersonalArtworkAccessUseCase;
  @Mock private PersonalArtworkFeelingRepository personalArtworkFeelingRepository;
  @Mock private PersonalArtworkFeelingReplyRepository personalArtworkFeelingReplyRepository;
  @Mock private UserExistenceRepository userExistenceRepository;
  @InjectMocks private PersonalArtworkFeelingValidator validator;

  @Test
  void emptyAccessResultIsTreatedAsPersonalArtworkNotFound() {
    when(getPersonalArtworkAccessUseCase.getPersonalArtworkAccess(99L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> validator.validatePersonalArtworkExists(99L))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_NOT_FOUND));
  }

  @Test
  void deletedFeelingCanRemainAReplyTarget() {
    PersonalArtworkFeeling deletedFeeling =
        PersonalArtworkFeeling.create(1L, 2L, "삭제된 감상평", List.of());
    deletedFeeling.delete();

    assertThatCode(() -> validator.validateReplyTarget(deletedFeeling, 1L))
        .doesNotThrowAnyException();
  }

  @Test
  void deletedFeelingCannotRemainAFeelingTarget() {
    PersonalArtworkFeeling deletedFeeling =
        PersonalArtworkFeeling.create(1L, 2L, "삭제된 감상평", List.of());
    deletedFeeling.delete();

    assertThatThrownBy(() -> validator.validateFeelingTarget(deletedFeeling, 1L))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(PersonalArtworkCommunicationErrorCode.PERSONAL_FEELING_NOT_FOUND));
  }
}
