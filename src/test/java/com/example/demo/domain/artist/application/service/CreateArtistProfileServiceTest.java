package com.example.demo.domain.artist.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artist.application.command.CreateArtistProfileCommand;
import com.example.demo.domain.artist.application.permission.ArtistPermissionChecker;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.repository.AreaOfActivityRepository;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import com.example.demo.domain.artist.domain.type.ActivityCategory;
import com.example.demo.domain.artist.presentation.mapper.ArtistProfileMapper;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CreateArtistProfileServiceTest {

  private final UserRepository userRepository = mock(UserRepository.class);
  private final ArtistProfileRepository artistProfileRepository =
      mock(ArtistProfileRepository.class);
  private final AreaOfActivityRepository areaOfActivityRepository =
      mock(AreaOfActivityRepository.class);
  private final ArtistProfileMapper artistProfileMapper = mock(ArtistProfileMapper.class);
  private final CreateArtistProfileService service =
      new CreateArtistProfileService(
          userRepository,
          artistProfileRepository,
          areaOfActivityRepository,
          artistProfileMapper,
          new ArtistPermissionChecker());

  @Test
  void completesArtistVerificationAfterCreatingProfile() {
    Long userId = 1L;
    User user =
        User.builder()
            .id(userId)
            .isVerified(false)
            .schoolEmail("student@du.ac.kr")
            .univName("디유대학교")
            .build();
    CreateArtistProfileCommand command =
        CreateArtistProfileCommand.builder()
            .artistName("artist")
            .activityCategories(List.of(ActivityCategory.PAINTING))
            .build();
    ArtistProfile profile = mock(ArtistProfile.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(artistProfileRepository.findByUser(user)).thenReturn(Optional.empty());
    when(artistProfileRepository.existsByArtistName("artist")).thenReturn(false);
    when(artistProfileMapper.toEntity(user, command)).thenReturn(profile);
    when(artistProfileRepository.save(profile)).thenReturn(profile);

    ArtistProfile result = service.execute(userId, command);

    assertThat(result).isSameAs(profile);
    assertThat(user.isVerified()).isTrue();
    verify(artistProfileRepository).save(profile);
  }
}
