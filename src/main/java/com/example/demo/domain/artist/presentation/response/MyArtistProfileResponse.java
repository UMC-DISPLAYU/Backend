package com.example.demo.domain.artist.presentation.response;

import com.example.demo.domain.artist.domain.type.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MyArtistProfileResponse(
    String profileImageUrl,
    String artistName,
    String introduction,
    String status,
    String schoolName,
    @Schema(description = "Portfolio 링크(기존 externalLink 필드)") String externalLink,
    @Schema(description = "Instagram 링크") String instagramLink,
    List<ActivityCategory> fields,
    @Schema(description = "작가 인증 여부", example = "true") boolean isVerified) {}
