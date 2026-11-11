package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.query.UserSearchQueryRepository;
import com.example.demo.domain.user.application.result.UserSearchResult;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchUserService {

  private final UserSearchQueryRepository userSearchQueryRepository;

  @Transactional(readOnly = true)
  public UserSearchResult execute(String nickname) {
    // 닉네임은 고유하므로 부분 검색 목록이 아닌 정확히 일치하는 사용자 한 명만 조회한다.
    return userSearchQueryRepository
        .findByNickname(nickname.trim())
        .map(result -> new UserSearchResult(result.userId(), result.name(), result.nickname()))
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NICKNAME_NOT_FOUND));
  }
}
