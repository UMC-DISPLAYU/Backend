package com.example.demo.domain.user.application.query;

import java.util.Optional;

public interface UserSearchQueryRepository {

  Optional<UserSearchQueryResult> findByNickname(String nickname);
}
