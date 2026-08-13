package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.PreviewFilterType;
import com.example.demo.domain.displayartwork.infrastructure.persistence.SpringDataDisplayArtworkJpaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;

/** 분야 필터를 하나도 고르지 않으면 빈 목록이 들어오는데, 그대로 IN 조건에 넘기면 쿼리가 깨진다. 어댑터가 이를 플래그로 바꿔 넘기는지 확인한다. */
class JpaDisplayArtworkRepositoryAdapterTest {

  private final SpringDataDisplayArtworkJpaRepository jpaRepository =
      mock(SpringDataDisplayArtworkJpaRepository.class);
  private final JpaDisplayArtworkRepositoryAdapter adapter =
      new JpaDisplayArtworkRepositoryAdapter(jpaRepository);

  @Test
  void ignoresFieldFilterWhenFieldsAreNull() {
    stubFindPreview();

    adapter.findPreview(PreviewFilterType.RECOMMEND, null, null, 0, 20);

    assertThat(capturedIgnoreFields()).isTrue();
    // IN 조건은 건너뛰더라도 바인딩 파라미터 자체는 비어 있으면 안 된다.
    assertThat(capturedFields()).isNotEmpty();
  }

  @Test
  void ignoresFieldFilterWhenFieldsAreEmpty() {
    stubFindPreview();

    adapter.findPreview(PreviewFilterType.RECOMMEND, List.of(), null, 0, 20);

    assertThat(capturedIgnoreFields()).isTrue();
    assertThat(capturedFields()).isNotEmpty();
  }

  @Test
  void appliesFieldFilterWhenFieldsAreGiven() {
    stubFindPreview();
    List<ArtworkType> selected = List.of(ArtworkType.PAINTING, ArtworkType.DESIGN);

    adapter.findPreview(PreviewFilterType.RECOMMEND, selected, null, 0, 20);

    assertThat(capturedIgnoreFields()).isFalse();
    assertThat(capturedFields()).containsExactlyElementsOf(selected);
  }

  @Test
  void requiresGraduationOnlyForGraduationTab() {
    stubFindPreview();

    adapter.findPreview(PreviewFilterType.GRADUATION, null, null, 0, 20);

    assertThat(capturedRequireGraduation()).isTrue();
  }

  private void stubFindPreview() {
    when(jpaRepository.findPreview(
            anyBoolean(), anyBoolean(), any(), anyLong(), isNull(), any(Pageable.class)))
        .thenReturn(List.of());
  }

  private boolean capturedRequireGraduation() {
    return captureBooleans().get(0);
  }

  private boolean capturedIgnoreFields() {
    return captureBooleans().get(1);
  }

  private List<Boolean> captureBooleans() {
    ArgumentCaptor<Boolean> requireGraduation = ArgumentCaptor.forClass(Boolean.class);
    ArgumentCaptor<Boolean> ignoreFields = ArgumentCaptor.forClass(Boolean.class);
    verify(jpaRepository)
        .findPreview(
            requireGraduation.capture(),
            ignoreFields.capture(),
            any(),
            anyLong(),
            isNull(),
            any(Pageable.class));
    return List.of(requireGraduation.getValue(), ignoreFields.getValue());
  }

  @SuppressWarnings("unchecked")
  private List<ArtworkType> capturedFields() {
    ArgumentCaptor<List<ArtworkType>> fields = ArgumentCaptor.forClass(List.class);
    verify(jpaRepository)
        .findPreview(
            anyBoolean(), anyBoolean(), fields.capture(), anyLong(), isNull(), any(Pageable.class));
    return fields.getValue();
  }
}
