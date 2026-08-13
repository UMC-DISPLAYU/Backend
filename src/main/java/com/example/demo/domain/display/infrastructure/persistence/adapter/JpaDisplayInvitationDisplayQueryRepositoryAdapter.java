package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.DisplayInvitationDisplayQueryRepository;
import com.example.demo.domain.display.application.query.DisplayInvitationDisplayQueryResult;
import com.example.demo.domain.display.domain.aggregate.QDisplay;
import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.example.demo.domain.display.domain.entity.QDisplayInvitation;
import com.example.demo.domain.display.domain.type.DisplayInvitationStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayInvitationDisplayQueryRepositoryAdapter
    implements DisplayInvitationDisplayQueryRepository {

  private static final QDisplayInvitation invitation = QDisplayInvitation.displayInvitation;
  private static final QDisplay display = QDisplay.display;
  private static final QDisplayImage image = QDisplayImage.displayImage;

  private final JPAQueryFactory queryFactory;

  public JpaDisplayInvitationDisplayQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<DisplayInvitationDisplayQueryResult> findPendingInvitationDisplays(
      Long inviteeUserId) {
    return queryFactory
        .select(
            Projections.constructor(
                DisplayInvitationDisplayQueryResult.class,
                display.id,
                display.title,
                image.imageUrl,
                display.organization,
                display.department,
                display.period.startDate,
                display.period.endDate))
        .from(invitation)
        .join(invitation.display, display)
        .leftJoin(display.images, image)
        .on(QDisplayImageConditions.mainImage(image))
        .where(
            invitation.inviteeUserId.value.eq(inviteeUserId),
            invitation.status.eq(DisplayInvitationStatus.PENDING),
            display.deletedAt.isNull(),
            invitation.deletedAt.isNull())
        .orderBy(invitation.id.desc())
        .fetch();
  }
}
