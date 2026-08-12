package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.command.UpdateMyProfileCommand;
import com.example.demo.domain.user.application.result.UpdateMyProfileResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.domain.user.domain.vo.ProfileImageUrl;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UpdateMyProfileServiceTest {

  private static final Long USER_ID = 1L;
  private final UserRepository userRepository = mock(UserRepository.class);
  private final ChangeNicknameService changeNicknameService =
      new ChangeNicknameService(userRepository);
  private final UpdateMyProfileService service =
      new UpdateMyProfileService(userRepository, changeNicknameService);

  @Test
  void updatesNicknameAndProfileImageTogether() {
    User user = User.builder().id(USER_ID).nickname("oldName").build();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(userRepository.existsByNickname("newName")).thenReturn(false);

    UpdateMyProfileResult result =
        service.execute(
            new UpdateMyProfileCommand(
                USER_ID,
                ProfileImageUrl.ofNullable(
                    "https://d1tdgnysscm2va.cloudfront.net/images/user/profile.jpg"),
                Nickname.of("newName")));

    assertThat(result.nickname()).isEqualTo("newName");
    assertThat(result.profileImageUrl())
        .isEqualTo("https://d1tdgnysscm2va.cloudfront.net/images/user/profile.jpg");
    assertThat(user.getProfileImageUrl())
        .isEqualTo("https://d1tdgnysscm2va.cloudfront.net/images/user/profile.jpg");
  }

  @Test
  void removesProfileImageWithoutCheckingDuplicateForSameNickname() {
    User user =
        User.builder()
            .id(USER_ID)
            .nickname("sameName")
            .profileImageUrl("https://cdn.example.com/old.jpg")
            .build();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    UpdateMyProfileResult result =
        service.execute(
            new UpdateMyProfileCommand(
                USER_ID, ProfileImageUrl.ofNullable(null), Nickname.of("sameName")));

    assertThat(result.profileImageUrl()).isNull();
    verify(userRepository, never()).existsByNickname("sameName");
  }
}
