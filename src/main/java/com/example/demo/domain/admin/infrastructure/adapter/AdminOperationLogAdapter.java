package com.example.demo.domain.admin.infrastructure.adapter;

import com.example.demo.domain.admin.application.port.OperationLogPort;
import com.example.demo.domain.admin.application.query.OperationLogSearchQuery;
import com.example.demo.domain.admin.application.result.OperationLog;
import com.example.demo.domain.admin.application.result.OperationLogPage;
import com.example.demo.domain.admin.infrastructure.persistence.AdminOperationJpaEntity;
import com.example.demo.domain.admin.infrastructure.persistence.QAdminOperationJpaEntity;
import com.example.demo.domain.admin.infrastructure.persistence.SpringDataAdminOperationJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminOperationLogAdapter implements OperationLogPort {
  private static final QAdminOperationJpaEntity operation =
      QAdminOperationJpaEntity.adminOperationJpaEntity;

  private final SpringDataAdminOperationJpaRepository operations;
  private final JPAQueryFactory queryFactory;

  @Override
  @Transactional
  public OperationLog record(OperationLog log) {
    Objects.requireNonNull(log, "log must not be null.");
    Objects.requireNonNull(log.processedAt(), "processedAt must not be null.");
    return toResult(
        operations.save(
            new AdminOperationJpaEntity(
                log.targetType(),
                log.targetId(),
                log.action(),
                log.operatorId(),
                log.reason(),
                toLocalDateTime(log.processedAt()))));
  }

  @Override
  @Transactional(readOnly = true)
  public OperationLogPage search(OperationLogSearchQuery query) {
    List<AdminOperationJpaEntity> rows =
        queryFactory
            .selectFrom(operation)
            .where(
                query.q() == null ? null : operation.reason.containsIgnoreCase(query.q()),
                query.from() == null
                    ? null
                    : operation.processedAt.goe(toLocalDateTime(query.from())),
                query.to() == null ? null : operation.processedAt.lt(toLocalDateTime(query.to())),
                query.targetType() == null ? null : operation.targetType.eq(query.targetType()),
                query.targetId() == null ? null : operation.targetId.eq(query.targetId()),
                query.action() == null ? null : operation.action.eq(query.action()),
                query.operatorId() == null ? null : operation.operatorId.eq(query.operatorId()),
                query.cursor() == null ? null : operation.operationId.lt(query.cursor()))
            .orderBy(operation.operationId.desc())
            .limit(query.size() + 1L)
            .fetch();
    boolean hasNext = rows.size() > query.size();
    List<OperationLog> items = rows.stream().limit(query.size()).map(this::toResult).toList();
    return new OperationLogPage(items, hasNext ? items.getLast().operationId() : null, hasNext);
  }

  private OperationLog toResult(AdminOperationJpaEntity row) {
    return new OperationLog(
        row.getOperationId(),
        row.getTargetType(),
        row.getTargetId(),
        row.getAction(),
        row.getOperatorId(),
        row.getReason(),
        row.getProcessedAt().toInstant(ZoneOffset.UTC));
  }

  private static LocalDateTime toLocalDateTime(Instant value) {
    return LocalDateTime.ofInstant(value, ZoneOffset.UTC);
  }
}
