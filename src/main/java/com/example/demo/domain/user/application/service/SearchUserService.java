package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.query.UserSearchQueryRepository;
import com.example.demo.domain.user.application.result.UserSearchResult;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchUserService {

  private static final int SEARCH_LIMIT = 20;

  private final UserSearchQueryRepository userSearchQueryRepository;

  @Transactional(readOnly = true)
  public List<UserSearchResult> execute(String nickname) {
    List<UserSearchResult> results =
        userSearchQueryRepository.searchByNickname(nickname.trim(), SEARCH_LIMIT).stream()
            .map(result -> new UserSearchResult(result.userId(), result.name(), result.nickname()))
            .toList();
    if (results.isEmpty()) {
      throw new UserException(UserErrorCode.USER_NICKNAME_NOT_FOUND);
    }
    return results;
  }
}
