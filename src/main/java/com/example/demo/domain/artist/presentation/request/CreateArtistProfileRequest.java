package com.example.demo.domain.artist.presentation.request;

import com.example.demo.domain.artist.application.command.CreateArtistProfileCommand;
import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateArtistProfileRequest(
    @Schema(description = "작가명", example = "홍길동") @NotBlank String artistName,
    @Schema(
            description = "활동 분야. 중복 없이 최대 2개까지 입력할 수 있습니다.",
            example = "[\"PAINTING\", \"ILLUSTRATION\"]")
        @NotEmpty
        @Size(max = 2)
        List<ActivityCategory> activityFields) {

  public CreateArtistProfileCommand toCommand() {
    return CreateArtistProfileCommand.builder()
        .artistName(artistName)
        .activityCategories(activityFields)
        .build();
  }
}
