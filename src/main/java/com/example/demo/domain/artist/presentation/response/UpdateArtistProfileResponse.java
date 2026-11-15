package com.example.demo.domain.artist.presentation.response;

import com.example.demo.domain.artist.domain.type.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record UpdateArtistProfileResponse(
    @Schema(example = "https://cdn.displayu.co.kr/artists/21/profile.jpg", nullable = true)
        String profileImageUrl,
    @Schema(example = "김디유") String artistName,
    @Schema(example = "회화와 디지털 아트를 작업하는 작가입니다.", nullable = true) String introduction,
    @Schema(example = "[\"PAINTING\", \"ILLUSTRATION\"]") List<ActivityCategory> fields,
    @Schema(
            description = "Portfolio 링크(기존 externalLink 필드)",
            example = "https://portfolio.example.com/diyu",
            nullable = true)
        String externalLink,
    @Schema(
            description = "Instagram 링크",
            example = "https://www.instagram.com/diyu.artist",
            nullable = true)
        String instagramLink,
    @Schema(example = "동국대학교") String univName) {}
