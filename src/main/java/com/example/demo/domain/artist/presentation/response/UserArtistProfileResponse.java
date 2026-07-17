package com.example.demo.domain.artist.presentation.response;

import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import java.util.List;

public record UserArtistProfileResponse(
    String artistName, String schoolName, String portfolioUrl, List<ActivityCategory> fields) {}
