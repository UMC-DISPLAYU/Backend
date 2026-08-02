package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.application.query.UserSearchQueryRepository;
import com.example.demo.domain.user.application.query.UserSearchQueryResult;
import com.example.demo.domain.user.domain.aggregate.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserSearchQueryRepositoryAdapter implements UserSearchQueryRepository {

  private static final QUser user = QUser.user;

  private final JPAQueryFactory queryFactory;

  @Override
  public List<UserSearchQueryResult> searchByNickname(String nickname, int limit) {
    return queryFactory
        .select(
            Projections.constructor(UserSearchQueryResult.class, user.id, user.name, user.nickname))
        .from(user)
        .where(user.nickname.lower().contains(nickname.toLowerCase()), user.deletedAt.isNull())
        .orderBy(user.nickname.asc(), user.id.asc())
        .limit(limit)
        .fetch();
  }
}
