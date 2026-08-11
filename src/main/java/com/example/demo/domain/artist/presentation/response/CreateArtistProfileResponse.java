package com.example.demo.domain.artist.presentation.response;

import com.example.demo.domain.artist.domain.type.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record CreateArtistProfileResponse(
    @Schema(description = "작가 프로필 ID", example = "1") Long artistProfileId,
    @Schema(description = "작가명", example = "홍길동") String artistName,
    @Schema(description = "인증된 학교 이메일", example = "user@university.ac.kr") String schoolEmail,
    @Schema(description = "인증된 학교명", example = "덕성여자대학교") String univName,
    @Schema(description = "활동 분야", example = "[\"PAINTING\", \"ILLUSTRATION\"]")
        List<ActivityCategory> activityFields,
    @Schema(description = "작가 인증 여부", example = "true") boolean isVerified) {}
