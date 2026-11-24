package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.admin.application.port.UserQueryPort;
import com.example.demo.domain.admin.application.query.UserSearchQuery;
import com.example.demo.domain.admin.application.result.UserDetail;
import com.example.demo.domain.admin.application.result.UserPage;
import com.example.demo.domain.admin.application.result.UserSummary;
import com.example.demo.domain.user.domain.aggregate.QUser;
import com.example.demo.domain.user.domain.type.AccountStatus;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AdminUserQueryAdapter implements UserQueryPort {

  private static final QUser user = QUser.user;

  private final JPAQueryFactory queryFactory;

  public AdminUserQueryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  @Transactional(readOnly = true)
  public UserPage search(UserSearchQuery query) {
    List<Tuple> rows =
        queryFactory
            .select(user.id, user.nickname, user.socialEmail, user.accountStatus, user.createdAt)
            .from(user)
            .where(
                user.deletedAt.isNull(),
                searchContains(query.q()),
                accountStatusEq(query.accountStatus()),
                cursorBefore(query.cursor()))
            .orderBy(user.id.desc())
            .limit((long) query.size() + 1)
            .fetch();

    boolean hasNext = rows.size() > query.size();
    List<UserSummary> items = rows.stream().limit(query.size()).map(this::toSummary).toList();
    Long nextCursor = hasNext ? items.getLast().userId() : null;
    return new UserPage(items, nextCursor, hasNext);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetail getDetail(Long userId) {
    Tuple row =
        queryFactory
            .select(
                user.id,
                user.nickname,
                user.socialEmail,
                user.accountStatus,
                user.createdAt,
                user.suspendedAt,
                user.suspensionOperatorId,
                user.suspensionReason)
            .from(user)
            .where(user.id.eq(userId), user.deletedAt.isNull())
            .fetchOne();
    if (row == null) {
      throw new BusinessException(GlobalErrorCode.NOT_FOUND);
    }

    AccountStatus status = row.get(user.accountStatus);
    boolean suspended = status == AccountStatus.SUSPENDED;
    return new UserDetail(
        row.get(user.id),
        row.get(user.nickname),
        row.get(user.socialEmail),
        status.name(),
        toInstant(row.get(user.createdAt)),
        suspended ? toInstant(row.get(user.suspendedAt)) : null,
        suspended ? row.get(user.suspensionOperatorId) : null,
        suspended ? row.get(user.suspensionReason) : null,
        List.of());
  }

  private UserSummary toSummary(Tuple row) {
    AccountStatus status = row.get(user.accountStatus);
    return new UserSummary(
        row.get(user.id),
        row.get(user.nickname),
        row.get(user.socialEmail),
        status.name(),
        toInstant(row.get(user.createdAt)));
  }

  private BooleanExpression searchContains(String searchWord) {
    if (searchWord == null) {
      return null;
    }
    String normalized = searchWord.toLowerCase(Locale.ROOT);
    return user.nickname
        .lower()
        .contains(normalized)
        .or(user.socialEmail.lower().contains(normalized));
  }

  private BooleanExpression accountStatusEq(String accountStatus) {
    return accountStatus == null
        ? null
        : user.accountStatus.eq(AccountStatus.valueOf(accountStatus));
  }

  private BooleanExpression cursorBefore(Long cursor) {
    return cursor == null ? null : user.id.lt(cursor);
  }

  private Instant toInstant(LocalDateTime value) {
    return value == null ? null : value.toInstant(ZoneOffset.UTC);
  }
}
