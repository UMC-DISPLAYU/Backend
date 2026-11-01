package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.application.query.UserSearchQueryRepository;
import com.example.demo.domain.user.application.query.UserSearchQueryResult;
import com.example.demo.domain.user.domain.aggregate.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserSearchQueryRepositoryAdapter implements UserSearchQueryRepository {

  private static final QUser user = QUser.user;

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<UserSearchQueryResult> findByNickname(String nickname) {
    UserSearchQueryResult result =
        queryFactory
            .select(
                Projections.constructor(
                    UserSearchQueryResult.class, user.id, user.name, user.nickname))
            .from(user)
            .where(user.nickname.eq(nickname), user.deletedAt.isNull())
            .fetchOne();
    return Optional.ofNullable(result);
  }
}
