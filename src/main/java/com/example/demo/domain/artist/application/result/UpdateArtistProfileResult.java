package com.example.demo.domain.artist.application.result;

import com.example.demo.domain.artist.domain.type.ActivityCategory;
import java.util.List;

public record UpdateArtistProfileResult(
    String profileImageUrl,
    String artistName,
    String introduction,
    List<ActivityCategory> fields,
    String externalLink,
    String univName) {}
