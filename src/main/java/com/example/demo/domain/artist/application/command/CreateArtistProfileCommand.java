package com.example.demo.domain.artist.application.command;

import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CreateArtistProfileCommand {

    private String artistName;

    private List<ActivityCategory> activityCategories;
}
