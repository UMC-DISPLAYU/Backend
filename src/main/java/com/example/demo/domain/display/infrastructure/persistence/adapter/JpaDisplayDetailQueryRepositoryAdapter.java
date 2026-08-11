package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.DisplayDetailQueryRepository;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.domain.aggregate.QDisplay;
import com.example.demo.domain.display.domain.entity.QDisplayContent;
import com.example.demo.domain.display.domain.entity.QDisplayContentCategory;
import com.example.demo.domain.display.domain.entity.QDisplayFieldSelection;
import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.example.demo.domain.display.domain.entity.QDisplayInvitation;
import com.example.demo.domain.display.domain.entity.QDisplayLike;
import com.example.demo.domain.display.domain.entity.QTeamMember;
import com.example.demo.domain.display.domain.type.DisplayContentStatus;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayDetailQueryRepositoryAdapter implements DisplayDetailQueryRepository {

  private static final QDisplay display = QDisplay.display;
  private static final QDisplayLike displayLike = QDisplayLike.displayLike;
  private static final QDisplayImage image = QDisplayImage.displayImage;
  private static final QDisplayFieldSelection fieldSelection =
      QDisplayFieldSelection.displayFieldSelection;
  private static final QDisplayContentCategory contentCategory =
      QDisplayContentCategory.displayContentCategory;
  private static final QDisplayContent content = QDisplayContent.displayContent;
  private static final QTeamMember teamMember = QTeamMember.teamMember;
  private static final QDisplayInvitation invitation = QDisplayInvitation.displayInvitation;

  private final JPAQueryFactory queryFactory;

  public JpaDisplayDetailQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Optional<DisplayDetailResult> findDisplayDetail(Long displayId, Long requesterUserId) {
    Tuple base = fetchBase(displayId);
    if (base == null) {
      return Optional.empty();
    }

    List<DisplayDetailResult.ImageResult> images = fetchImages(displayId);
    List<String> fields = fetchFields(displayId);
    List<DisplayDetailResult.TeamMemberResult> teamMembers = fetchTeamMembers(displayId);
    boolean canViewDraft = canViewDraft(base, teamMembers, requesterUserId);
    if (!canViewDraft && base.get(display.status) != DisplayStatus.PUBLISHED) {
      return Optional.empty();
    }
    List<DisplayDetailResult.ContentCategoryResult> contentCategories =
        fetchContentCategories(displayId, canViewDraft);
    List<DisplayDetailResult.InvitationResult> invitations = fetchInvitations(displayId);

    return Optional.of(toResult(base, fields, images, contentCategories, teamMembers, invitations));
  }

  private Tuple fetchBase(Long displayId) {
    return queryFactory
        .select(
            display.id,
            display.ownerUserId.value,
            display.title,
            display.subtitle,
            display.content,
            display.location.placeName,
            display.location.latitude,
            display.location.longitude,
            display.location.roadAddress,
            display.qnaAccount,
            display.contract,
            display.note,
            display.organization,
            display.department,
            display.displayType,
            display.region,
            display.period.startDate,
            display.period.endDate,
            display.period.startTime,
            display.period.endTime,
            display.artworkContentOpen,
            display.exhibitionContentOpen,
            display.status,
            display.invitationToken,
            display.invitationDisabledAt,
            displayLike.id.count())
        .from(display)
        .leftJoin(displayLike)
        .on(displayLike.displayId.eq(display.id))
        .where(display.id.eq(displayId))
        .groupBy(display.id)
        .fetchOne();
  }

  private List<DisplayDetailResult.ImageResult> fetchImages(Long displayId) {
    return queryFactory
        .select(
            Projections.constructor(
                DisplayDetailResult.ImageResult.class,
                image.id,
                image.imageUrl,
                image.imageType.stringValue(),
                image.sortOrder))
        .from(image)
        .where(image.display.id.eq(displayId), image.deletedAt.isNull())
        .orderBy(image.sortOrder.asc(), image.id.asc())
        .fetch();
  }

  private List<String> fetchFields(Long displayId) {
    return queryFactory
        .select(fieldSelection.field.stringValue())
        .from(fieldSelection)
        .where(fieldSelection.display.id.eq(displayId))
        .orderBy(fieldSelection.sortOrder.asc(), fieldSelection.id.asc())
        .fetch();
  }

  private List<DisplayDetailResult.ContentCategoryResult> fetchContentCategories(
      Long displayId, boolean includeDraft) {
    List<Tuple> categories =
        queryFactory
            .select(
                contentCategory.id,
                contentCategory.name,
                contentCategory.description,
                contentCategory.sortOrder)
            .from(contentCategory)
            .where(contentCategory.display.id.eq(displayId))
            .orderBy(contentCategory.sortOrder.asc(), contentCategory.id.asc())
            .fetch();

    Map<Long, List<DisplayDetailResult.ContentResult>> contentsByCategoryId =
        fetchContents(
            categories.stream().map(tuple -> tuple.get(contentCategory.id)).toList(), includeDraft);

    return categories.stream()
        .map(
            tuple ->
                new DisplayDetailResult.ContentCategoryResult(
                    tuple.get(contentCategory.id),
                    tuple.get(contentCategory.name),
                    tuple.get(contentCategory.description),
                    tuple.get(contentCategory.sortOrder),
                    contentsByCategoryId.getOrDefault(
                        tuple.get(contentCategory.id), Collections.emptyList())))
        .toList();
  }

  private Map<Long, List<DisplayDetailResult.ContentResult>> fetchContents(
      List<Long> categoryIds, boolean includeDraft) {
    if (categoryIds.isEmpty()) {
      return Collections.emptyMap();
    }

    List<Tuple> contents =
        queryFactory
            .select(content.category.id, content.id, content.imageUrl, content.sortOrder)
            .from(content)
            .where(
                content.category.id.in(categoryIds),
                includeDraft ? null : content.status.eq(DisplayContentStatus.PUBLISHED))
            .orderBy(content.category.id.asc(), content.sortOrder.asc(), content.id.asc())
            .fetch();

    return contents.stream()
        .collect(
            Collectors.groupingBy(
                tuple -> tuple.get(content.category.id),
                Collectors.mapping(
                    tuple ->
                        new DisplayDetailResult.ContentResult(
                            tuple.get(content.id),
                            tuple.get(content.imageUrl),
                            tuple.get(content.sortOrder)),
                    Collectors.toList())));
  }

  private List<DisplayDetailResult.TeamMemberResult> fetchTeamMembers(Long displayId) {
    return queryFactory
        .select(
            Projections.constructor(
                DisplayDetailResult.TeamMemberResult.class,
                teamMember.id,
                teamMember.userId.value,
                teamMember.displayNickname,
                teamMember.role.stringValue(),
                teamMember.accepted))
        .from(teamMember)
        .where(teamMember.display.id.eq(displayId), teamMember.deletedAt.isNull())
        .orderBy(teamMember.id.asc())
        .fetch();
  }

  private List<DisplayDetailResult.InvitationResult> fetchInvitations(Long displayId) {
    return queryFactory
        .select(
            Projections.constructor(
                DisplayDetailResult.InvitationResult.class,
                invitation.id,
                invitation.inviterUserId.value,
                invitation.inviteeUserId.value,
                invitation.createdAt))
        .from(invitation)
        .where(invitation.display.id.eq(displayId), invitation.deletedAt.isNull())
        .orderBy(invitation.createdAt.asc(), invitation.id.asc())
        .fetch();
  }

  private boolean canViewDraft(
      Tuple base, List<DisplayDetailResult.TeamMemberResult> teamMembers, Long requesterUserId) {
    if (requesterUserId == null) {
      return false;
    }
    Long ownerUserId = base.get(display.ownerUserId.value);
    return requesterUserId.equals(ownerUserId)
        || teamMembers.stream()
            .anyMatch(
                teamMember -> teamMember.accepted() && requesterUserId.equals(teamMember.userId()));
  }

  private DisplayDetailResult toResult(
      Tuple base,
      List<String> fields,
      List<DisplayDetailResult.ImageResult> images,
      List<DisplayDetailResult.ContentCategoryResult> contentCategories,
      List<DisplayDetailResult.TeamMemberResult> teamMembers,
      List<DisplayDetailResult.InvitationResult> invitations) {
    return new DisplayDetailResult(
        base.get(display.id),
        base.get(display.ownerUserId.value),
        base.get(display.title),
        base.get(display.subtitle),
        base.get(display.content),
        new DisplayDetailResult.LocationResult(
            base.get(display.location.placeName),
            base.get(display.location.latitude),
            base.get(display.location.longitude),
            base.get(display.location.roadAddress)),
        base.get(display.qnaAccount),
        base.get(display.contract),
        base.get(display.note),
        base.get(display.organization),
        base.get(display.department),
        base.get(display.displayType).name(),
        fields,
        base.get(display.region).name(),
        base.get(displayLike.id.count()),
        false,
        new DisplayDetailResult.PeriodResult(
            base.get(display.period.startDate),
            base.get(display.period.endDate),
            base.get(display.period.startTime),
            base.get(display.period.endTime)),
        base.get(display.artworkContentOpen).name(),
        base.get(display.exhibitionContentOpen).name(),
        base.get(display.status).name(),
        base.get(display.invitationToken),
        base.get(display.invitationDisabledAt),
        images,
        contentCategories,
        teamMembers,
        invitations);
  }
}
