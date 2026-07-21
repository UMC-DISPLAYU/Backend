package com.example.demo.domain.artist.application.command;

import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import java.util.List;

public record UpdateArtistProfileCommand(
    Long userId,
    String nickname,
    String introduction,
    List<ActivityCategory> fields,
    String externalLink,
    String univName) {}
