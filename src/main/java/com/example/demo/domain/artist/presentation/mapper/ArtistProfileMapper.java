package com.example.demo.domain.artist.presentation.mapper;

import com.example.demo.domain.artist.application.command.CreateArtistProfileCommand;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import com.example.demo.domain.artist.presentation.response.CreateArtistProfileResponse;
import com.example.demo.domain.user.domain.aggregate.User;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ArtistProfileMapper {

  public ArtistProfile toEntity(User user, CreateArtistProfileCommand command) {
    return ArtistProfile.create(
        user, command.getArtistName(), user.getSchoolEmail(), user.getUnivName(), null);
  }

  public CreateArtistProfileResponse toResponse(
      ArtistProfile profile, List<ActivityCategory> activityFields) {
    return new CreateArtistProfileResponse(
        profile.getId(),
        profile.getArtistName(),
        profile.getSchoolEmail(),
        profile.getUnivName(),
        activityFields);
  }
}
