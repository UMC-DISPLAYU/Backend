package com.example.demo.domain.artist.presentation.request;

import com.example.demo.domain.artist.application.command.CreateArtistProfileCommand;
import com.example.demo.domain.artist.domain.type.ActivityCategory;
import com.example.demo.domain.artist.domain.vo.ArtistName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateArtistProfileRequest(
    @Schema(description = "한글, 영문, 숫자만 사용한 2~15자 작가명", example = "홍길동")
        @NotNull(message = ArtistName.INVALID_MESSAGE) @Pattern(regexp = ArtistName.PATTERN_VALUE, message = ArtistName.INVALID_MESSAGE) String artistName,
    @Schema(
            description =
                "활동 분야. 중복 없이 최대 2개까지 입력할 수 있습니다. 선택 가능한 분야: PAINTING, SCULPTURE, PHOTOGRAPHY, GRAPHIC, BRANDING, PRODUCT, VIDEO, SPACE, ARCHITECTURE, UX_UI, CRAFTS, OTHERS. UXUI/MEDIA/CRAFT/ETC 입력은 UX_UI/VIDEO/CRAFTS/OTHERS로 응답합니다. 기존 DESIGN/FASHION 등은 조회 및 변경 없이 유지할 때만 지원합니다.",
            example = "[\"PAINTING\", \"GRAPHIC\"]")
        @NotEmpty @Size(max = 2) List<@NotNull ActivityCategory> activityFields) {

  public CreateArtistProfileCommand toCommand() {
    return CreateArtistProfileCommand.builder()
        .artistName(artistName)
        .activityCategories(activityFields)
        .build();
  }
}
