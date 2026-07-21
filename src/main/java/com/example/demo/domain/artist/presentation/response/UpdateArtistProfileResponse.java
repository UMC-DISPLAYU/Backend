package com.example.demo.domain.artist.presentation.response;

import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import java.util.List;

public record UpdateArtistProfileResponse(
    String nickname,
    String introduction,
    List<ActivityCategory> fields,
    String externalLink,
    String univName) {}
