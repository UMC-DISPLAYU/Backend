package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.result.UserSearchResult;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchUserService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public List<UserSearchResult> execute(String nickname) {
    List<UserSearchResult> results =
        userRepository.searchByNickname(nickname.trim()).stream()
            .map(user -> new UserSearchResult(user.getId(), user.getName(), user.getNickname()))
            .toList();
    if (results.isEmpty()) {
      throw new UserException(UserErrorCode.USER_NICKNAME_NOT_FOUND);
    }
    return results;
  }
}
