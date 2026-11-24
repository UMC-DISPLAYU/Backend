package com.example.demo.domain.display.presentation.response;

import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.display.domain.type.PosterFit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record ArtistDisplayListResponse(
    List<ArtistDisplayResponse> createdDisplays, List<ArtistDisplayResponse> participatedDisplays) {

  public record ArtistDisplayResponse(
      Long displayId,
      String title,
      String displayStatus,
      DisplayStatus publishStatus,
      LocalDate startDate,
      LocalDate endDate,
      String school,
      String department,
      String placeName,
      String postImageUrl,
      boolean isLeader,
      PosterCropResponse posterCrop,
      PosterFit posterFit,
      @Schema(
              types = {"string", "null"},
              pattern = "^#[0-9a-fA-F]{6}$")
          String posterEdgeColor) {}
}
