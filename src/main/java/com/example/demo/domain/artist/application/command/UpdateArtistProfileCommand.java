package com.example.demo.domain.artist.application.command;

import com.example.demo.domain.artist.domain.type.ActivityCategory;
import java.util.List;

public record UpdateArtistProfileCommand(
    Long userId,
    String profileImageUrl,
    String artistName,
    String introduction,
    List<ActivityCategory> fields,
    String externalLink,
    String univName) {}
