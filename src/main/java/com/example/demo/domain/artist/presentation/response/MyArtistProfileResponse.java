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
    String externalLink,
    List<ActivityCategory> fields,
    @Schema(description = "작가 인증 여부", example = "true") boolean isVerified) {}
