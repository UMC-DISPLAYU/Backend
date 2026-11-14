package com.example.demo.global.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.artist.domain.type.ActivityCategory;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class CreativeFieldCodesTest {
  @Test
  void allDomainsExposeExactlyTheSameTwelveSelectableCodes() {
    Set<String> expected =
        Set.of(
            "PAINTING",
            "SCULPTURE",
            "PHOTOGRAPHY",
            "GRAPHIC",
            "BRANDING",
            "PRODUCT",
            "VIDEO",
            "SPACE",
            "ARCHITECTURE",
            "UX_UI",
            "CRAFTS",
            "OTHERS");
    assertThat(
            Arrays.stream(ActivityCategory.values())
                .filter(ActivityCategory::isSelectable)
                .map(ActivityCategory::canonical)
                .map(Enum::name)
                .collect(Collectors.toSet()))
        .isEqualTo(expected);
    assertThat(
            Arrays.stream(DisplayField.values())
                .filter(DisplayField::isSelectable)
                .map(DisplayField::canonical)
                .map(Enum::name)
                .collect(Collectors.toSet()))
        .isEqualTo(expected);
    assertThat(
            Arrays.stream(ArtworkType.values())
                .filter(ArtworkType::isSelectable)
                .map(ArtworkType::canonical)
                .map(Enum::name)
                .collect(Collectors.toSet()))
        .isEqualTo(expected);
    assertThat(ArtworkType.VIDEO.storedAliases())
        .containsExactlyInAnyOrder(ArtworkType.VIDEO, ArtworkType.MEDIA);
    assertThat(DisplayField.UX_UI.storedAliases())
        .containsExactlyInAnyOrder(DisplayField.UX_UI, DisplayField.UXUI);
  }
}
