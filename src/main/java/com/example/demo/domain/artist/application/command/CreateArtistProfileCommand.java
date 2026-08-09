package com.example.demo.domain.artist.application.command;

import com.example.demo.domain.artist.domain.type.ActivityCategory;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateArtistProfileCommand {

  private String artistName;

  private List<ActivityCategory> activityCategories;
}
