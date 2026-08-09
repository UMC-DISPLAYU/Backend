package com.example.demo.domain.artist.presentation.response;

import com.example.demo.domain.artist.domain.type.ActivityCategory;
import java.util.List;

public record UpdateArtistProfileResponse(
    String profileImageUrl,
    String artistName,
    String introduction,
    List<ActivityCategory> fields,
    String externalLink,
    String univName) {}
