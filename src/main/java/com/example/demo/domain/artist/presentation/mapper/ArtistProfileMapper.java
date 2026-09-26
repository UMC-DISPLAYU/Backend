package com.example.demo.domain.artist.presentation.mapper;

import com.example.demo.domain.artist.application.command.CreateArtistProfileCommand;
import com.example.demo.domain.artist.application.command.UpdateArtistProfileCommand;
import com.example.demo.domain.artist.application.result.ArtistProfileResult;
import com.example.demo.domain.artist.application.result.UpdateArtistProfileResult;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.type.ActivityCategory;
import com.example.demo.domain.artist.presentation.request.UpdateArtistProfileRequest;
import com.example.demo.domain.artist.presentation.response.CreateArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.MyArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.UpdateArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.UserArtistProfileResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ArtistProfileMapper {

  public ArtistProfile toEntity(
      Long userId, String schoolEmail, String univName, CreateArtistProfileCommand command) {
    return ArtistProfile.create(userId, command.getArtistName(), schoolEmail, univName, null);
  }

  public CreateArtistProfileResponse toResponse(
      ArtistProfile profile, List<ActivityCategory> activityFields) {
    return new CreateArtistProfileResponse(
        profile.getId(),
        profile.getArtistName(),
        profile.getSchoolEmail(),
        profile.getUnivName(),
        activityFields,
        true);
  }

  public MyArtistProfileResponse toMyResponse(ArtistProfileResult result) {
    return new MyArtistProfileResponse(
        result.profileImageUrl(),
        result.artistName(),
        result.introduction(),
        "VERIFIED",
        result.schoolName(),
        result.externalLink(),
        result.fields(),
        result.isVerified());
  }

  public UserArtistProfileResponse toUserResponse(ArtistProfileResult result) {
    return new UserArtistProfileResponse(
        result.profileImageUrl(),
        result.artistName(),
        result.introduction(),
        result.schoolName(),
        result.externalLink(),
        result.fields());
  }

  public UpdateArtistProfileCommand toCommand(Long userId, UpdateArtistProfileRequest request) {
    return new UpdateArtistProfileCommand(
        userId,
        request.profileImageUrl(),
        request.artistName(),
        request.introduction(),
        request.fields(),
        request.externalLink(),
        request.univName());
  }

  public UpdateArtistProfileResponse toResponse(UpdateArtistProfileResult result) {
    return new UpdateArtistProfileResponse(
        result.profileImageUrl(),
        result.artistName(),
        result.introduction(),
        result.fields(),
        result.externalLink(),
        result.univName());
  }
}
