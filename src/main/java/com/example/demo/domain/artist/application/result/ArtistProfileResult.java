package com.example.demo.domain.artist.application.result;

import com.example.demo.domain.artist.domain.type.ActivityCategory;
import java.util.List;

public record ArtistProfileResult(
    String profileImageUrl,
    String artistName,
    String introduction,
    String schoolName,
    String externalLink,
    List<ActivityCategory> fields,
    boolean isVerified) {}
