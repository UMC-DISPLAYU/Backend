package com.example.demo.domain.artist.presentation.response;

import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import java.util.List;

public record MyArtistProfileResponse(
    String artistName,
    String status,
    String schoolName,
    String portfolioUrl,
    List<ActivityCategory> fields) {}
