package com.example.demo.domain.artist.application.query;

import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import java.util.List;

public record ArtistProfileSummaryQueryResult(
    Long artistProfileId,
    Long userId,
    String artistName,
    String profileImageUrl,
    List<ActivityCategory> fields) {}
