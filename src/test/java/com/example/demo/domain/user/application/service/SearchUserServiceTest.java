package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.query.UserSearchQueryRepository;
import com.example.demo.domain.user.application.query.UserSearchQueryResult;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchUserServiceTest {

  private final UserSearchQueryRepository userSearchQueryRepository =
      mock(UserSearchQueryRepository.class);
  private final SearchUserService service = new SearchUserService(userSearchQueryRepository);

  @Test
  void trimsNicknameAndMapsSearchResults() {
    UserSearchQueryResult queryResult = new UserSearchQueryResult(12L, "이정우", "quietroom");
    when(userSearchQueryRepository.searchByNickname("quiet", 20)).thenReturn(List.of(queryResult));

    var results = service.execute("  quiet  ");

    assertThat(results)
        .singleElement()
        .satisfies(
            result -> {
              assertThat(result.userId()).isEqualTo(12L);
              assertThat(result.name()).isEqualTo("이정우");
              assertThat(result.nickname()).isEqualTo("quietroom");
            });
    verify(userSearchQueryRepository).searchByNickname("quiet", 20);
  }

  @Test
  void throwsExceptionWhenNoUserMatches() {
    when(userSearchQueryRepository.searchByNickname("unknown", 20)).thenReturn(List.of());

    assertThatThrownBy(() -> service.execute("unknown"))
        .isInstanceOf(UserException.class)
        .satisfies(
            exception ->
                assertThat(((UserException) exception).errorCode())
                    .isEqualTo(UserErrorCode.USER_NICKNAME_NOT_FOUND));
  }
}
