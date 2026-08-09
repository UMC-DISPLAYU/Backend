package com.example.demo.domain.artist.presentation.response;

import com.example.demo.domain.artist.domain.type.ActivityCategory;
import java.util.List;

public record MyArtistProfileResponse(
    String profileImageUrl,
    String artistName,
    String introduction,
    String status,
    String schoolName,
    String externalLink,
    List<ActivityCategory> fields) {}
