package com.example.demo.domain.artist.presentation.request;

import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record UpdateArtistProfileRequest(
    @Schema(example = "sangjun24") String nickname,
    @Schema(example = "시각과 공간의 관계를 탐구하는 작가입니다.") String introduction,
    @Schema(example = "[\"DESIGN\", \"VIDEO\"]") List<ActivityCategory> fields,
    @Schema(example = "https://portfolio.sangjun.com") String externalLink,
    @Schema(example = "한양대학교") String univName) {}
