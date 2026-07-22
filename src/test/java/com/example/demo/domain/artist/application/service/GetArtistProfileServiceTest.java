package com.example.demo.domain.artist.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artist.application.result.ArtistProfileResult;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.repository.AreaOfActivityRepository;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetArtistProfileServiceTest {

  private final ArtistProfileRepository artistProfileRepository =
      mock(ArtistProfileRepository.class);
  private final AreaOfActivityRepository areaOfActivityRepository =
      mock(AreaOfActivityRepository.class);
  private final GetArtistProfileService service =
      new GetArtistProfileService(artistProfileRepository, areaOfActivityRepository);

  @Test
  void returnsIntroductionExternalLinkAndProfileImage() {
    User user =
        User.builder().id(1L).profileImageUrl("https://cdn.example.com/profile.jpg").build();
    ArtistProfile profile =
        ArtistProfile.create(
            user, "maya", "artist@du.ac.kr", "덕성여자대학교", "https://portfolio.maya.com");
    profile.updateProfile("시각과 공간의 관계를 탐구하는 작가입니다.", "https://portfolio.maya.com", "덕성여자대학교");
    when(artistProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
    when(areaOfActivityRepository.findByArtistProfile(profile)).thenReturn(List.of());

    ArtistProfileResult result = service.getMine(1L);

    assertThat(result.profileImageUrl()).isEqualTo("https://cdn.example.com/profile.jpg");
    assertThat(result.artistName()).isEqualTo("maya");
    assertThat(result.introduction()).isEqualTo("시각과 공간의 관계를 탐구하는 작가입니다.");
    assertThat(result.externalLink()).isEqualTo("https://portfolio.maya.com");
  }
}
