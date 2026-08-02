package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchUserServiceTest {

  private final UserRepository userRepository = mock(UserRepository.class);
  private final SearchUserService service = new SearchUserService(userRepository);

  @Test
  void trimsNicknameAndMapsSearchResults() {
    User user = User.builder().id(12L).name("이정우").nickname("quietroom").build();
    when(userRepository.searchByNickname("quiet")).thenReturn(List.of(user));

    var results = service.execute("  quiet  ");

    assertThat(results)
        .singleElement()
        .satisfies(
            result -> {
              assertThat(result.userId()).isEqualTo(12L);
              assertThat(result.name()).isEqualTo("이정우");
              assertThat(result.nickname()).isEqualTo("quietroom");
            });
    verify(userRepository).searchByNickname("quiet");
  }

  @Test
  void throwsExceptionWhenNoUserMatches() {
    when(userRepository.searchByNickname("unknown")).thenReturn(List.of());

    assertThatThrownBy(() -> service.execute("unknown"))
        .isInstanceOf(UserException.class)
        .satisfies(
            exception ->
                assertThat(((UserException) exception).errorCode())
                    .isEqualTo(UserErrorCode.USER_NICKNAME_NOT_FOUND));
  }
}
