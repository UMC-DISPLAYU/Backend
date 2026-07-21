package com.example.demo.domain.artist.presentation.request;

import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record UpdateArtistProfileRequest(
    @Schema(example = "https://cdn.example.com/images/user/profile.jpg") String profileImageUrl,
    @Schema(example = "maya") String nickname,
    @Schema(example = "시각과 공간의 관계를 탐구하는 작가입니다.") String introduction,
    @Schema(example = "[\"DESIGN\", \"VIDEO\"]") List<ActivityCategory> fields,
    @Schema(example = "https://portfolio.maya.com") String externalLink,
    @Schema(example = "덕성여자대학교") String univName) {}
