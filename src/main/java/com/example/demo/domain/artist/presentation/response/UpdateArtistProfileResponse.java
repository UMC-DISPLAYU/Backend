package com.example.demo.domain.artist.presentation.response;

import com.example.demo.domain.artist.domain.type.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record UpdateArtistProfileResponse(
    String profileImageUrl,
    String artistName,
    String introduction,
    List<ActivityCategory> fields,
    @Schema(description = "Portfolio 링크(기존 externalLink 필드)") String externalLink,
    @Schema(description = "Instagram 링크") String instagramLink,
    String univName) {}
