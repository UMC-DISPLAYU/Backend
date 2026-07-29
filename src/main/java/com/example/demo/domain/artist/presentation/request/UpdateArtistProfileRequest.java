package com.example.demo.domain.artist.presentation.request;

import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UpdateArtistProfileRequest(
    @Schema(example = "https://cdn.example.com/images/user/profile.jpg")
        @Size(max = 2048) @Pattern(regexp = "^https?://[^\\s/$.?#].[^\\s]*$", flags = Pattern.Flag.CASE_INSENSITIVE) String profileImageUrl,
    @Schema(example = "maya") @NotBlank @Size(max = 50) String artistName,
    @Schema(example = "시각과 공간의 관계를 탐구하는 작가입니다.") @Size(max = 100) String introduction,
    @Schema(example = "[\"DESIGN\", \"VIDEO\"]") @NotEmpty @Size(max = 2) List<@NotNull ActivityCategory> fields,
    @Schema(example = "https://portfolio.maya.com")
        @Size(max = 255) @Pattern(regexp = "^https?://[^\\s/$.?#].[^\\s]*$", flags = Pattern.Flag.CASE_INSENSITIVE) String externalLink,
    @Schema(description = "대학교 목록에 존재하며 인증 이메일 도메인과 일치하는 학교명", example = "덕성여자대학교")
        String univName) {}
