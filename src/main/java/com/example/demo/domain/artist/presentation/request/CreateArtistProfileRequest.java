package com.example.demo.domain.artist.presentation.request;

import com.example.demo.domain.artist.application.command.CreateArtistProfileCommand;
import com.example.demo.domain.artist.domain.type.ActivityCategory;
import com.example.demo.domain.artist.domain.vo.ArtistName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateArtistProfileRequest(
    @Schema(description = "한글, 영문, 숫자만 사용한 2~15자 작가명", example = "홍길동")
        @NotBlank @Pattern(regexp = ArtistName.PATTERN_VALUE, message = ArtistName.INVALID_MESSAGE) String artistName,
    @Schema(
            description = "활동 분야. 중복 없이 최대 2개까지 입력할 수 있습니다.",
            example = "[\"PAINTING\", \"ILLUSTRATION\"]")
        @NotEmpty @Size(max = 2) List<ActivityCategory> activityFields) {

  public CreateArtistProfileCommand toCommand() {
    return CreateArtistProfileCommand.builder()
        .artistName(artistName)
        .activityCategories(activityFields)
        .build();
  }
}
