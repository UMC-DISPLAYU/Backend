package com.example.demo.domain.user.application.query;

import java.util.List;

public interface UserSearchQueryRepository {

  List<UserSearchQueryResult> searchByNickname(String nickname, int limit);
}
