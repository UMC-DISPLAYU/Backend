package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.query.UserSearchQueryRepository;
import com.example.demo.domain.user.application.query.UserSearchQueryResult;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SearchUserServiceTest {

  private final UserSearchQueryRepository userSearchQueryRepository =
      mock(UserSearchQueryRepository.class);
  private final SearchUserService service = new SearchUserService(userSearchQueryRepository);

  @Test
  void trimsNicknameAndMapsExactSearchResult() {
    UserSearchQueryResult queryResult = new UserSearchQueryResult(12L, "홍길동", "quietroom");
    when(userSearchQueryRepository.findByNickname("quietroom"))
        .thenReturn(Optional.of(queryResult));

    var result = service.execute("  quietroom  ");

    assertThat(result.userId()).isEqualTo(12L);
    assertThat(result.name()).isEqualTo("홍길동");
    assertThat(result.nickname()).isEqualTo("quietroom");
    verify(userSearchQueryRepository).findByNickname("quietroom");
  }

  @Test
  void throwsExceptionWhenNoExactUserMatches() {
    when(userSearchQueryRepository.findByNickname("unknown")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.execute("unknown"))
        .isInstanceOf(UserException.class)
        .satisfies(
            exception ->
                assertThat(((UserException) exception).errorCode())
                    .isEqualTo(UserErrorCode.USER_NICKNAME_NOT_FOUND));
  }
}
