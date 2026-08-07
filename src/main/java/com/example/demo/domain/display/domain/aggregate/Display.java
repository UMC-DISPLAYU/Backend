package com.example.demo.domain.display.domain.aggregate;

import com.example.demo.domain.display.domain.entity.DisplayContent;
import com.example.demo.domain.display.domain.entity.DisplayContentCategory;
import com.example.demo.domain.display.domain.entity.DisplayFieldSelection;
import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayContentStatus;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.domain.type.DisplayInvitationStatus;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.global.entity.BaseTimeEntity;
import com.example.demo.global.error.BusinessException;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Display")
public class Display extends BaseTimeEntity {

  // 이미지 크기 입력 필드가 추가되기 전까지 대표 이미지의 임시 크기값으로 사용한다.
  private static final int DEFAULT_MAIN_IMAGE_WIDTH = 1;
  private static final int DEFAULT_MAIN_IMAGE_HEIGHT = 1;
  private static final int MAIN_IMAGE_SORT_ORDER = 0;

  // 식별자와 소유자 정보: 전시 Aggregate의 정체성과 생성/관리 주체를 나타낸다.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayId")
  private Long id;

  @Version private Long version;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "userId", nullable = false))
  private UserId ownerUserId;

  // 기본 소개 정보: 전시명, 부제, 소개 본문, 문의 계정, 안내 문구, 소속 정보를 저장한다.
  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String subtitle;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private String qnaAccount;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String note;

  @Column(nullable = false)
  private String organization;

  @Column(nullable = false)
  private String department;

  @Column(length = 50)
  private String contract;

  // 장소/일정/분류 정보: 전시 위치, 기간, 유형, 분야를 저장한다.
  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "placeName", column = @Column(name = "placeName", nullable = false)),
    @AttributeOverride(
        name = "latitude",
        column = @Column(name = "latitude", nullable = false, precision = 10, scale = 7)),
    @AttributeOverride(
        name = "longitude",
        column = @Column(name = "longitude", nullable = false, precision = 10, scale = 7))
  })
  private DisplayLocation location;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DisplayType displayType;

  @Embedded private DisplayPeriod period;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DisplayRegion region;

  // 공개 정책과 발행 상태: 작품/전시 콘텐츠 공개 시점과 초안/발행 상태를 관리한다.
  @Enumerated(EnumType.STRING)
  @Column(name = "artWorkContentOpen", nullable = false)
  private ContentOpenPolicy artworkContentOpen;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ContentOpenPolicy exhibitionContentOpen;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DisplayStatus status;

  // 팀원 초대 정보: 초대 토큰과 비활성화 시점을 저장한다.
  private String invitationToken;

  private LocalDateTime invitationDisabledAt;

  // 내부 엔티티 목록: 이미지, 소개 콘텐츠, 팀원, 초대를 Display Root가 직접 통제한다.
  @OneToMany(mappedBy = "display", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<DisplayImage> images = new ArrayList<>();

  @OneToMany(mappedBy = "display", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<DisplayContentCategory> contentCategories = new ArrayList<>();

  @OneToMany(mappedBy = "display", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<DisplayFieldSelection> fieldSelections = new ArrayList<>();

  @OneToMany(mappedBy = "display", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<TeamMember> teamMembers = new ArrayList<>();

  @OneToMany(mappedBy = "display", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<DisplayInvitation> invitations = new ArrayList<>();

  protected Display() {}

  public static Display create(
      UserId ownerUserId,
      String title,
      String posterImageUrl,
      String subtitle,
      String content,
      DisplayLocation location,
      String qnaAccount,
      String note,
      String organization,
      String department,
      DisplayType displayType,
      List<DisplayField> displayFields,
      DisplayPeriod period,
      ContentOpenPolicy artworkContentOpen,
      ContentOpenPolicy exhibitionContentOpen) {
    return create(
        ownerUserId,
        title,
        posterImageUrl,
        subtitle,
        content,
        location,
        qnaAccount,
        note,
        organization,
        department,
        null,
        displayType,
        displayFields,
        period,
        artworkContentOpen,
        exhibitionContentOpen);
  }

  public static Display create(
      UserId ownerUserId,
      String title,
      String posterImageUrl,
      String subtitle,
      String content,
      DisplayLocation location,
      String qnaAccount,
      String note,
      String organization,
      String department,
      String contract,
      DisplayType displayType,
      List<DisplayField> displayFields,
      DisplayPeriod period,
      ContentOpenPolicy artworkContentOpen,
      ContentOpenPolicy exhibitionContentOpen) {
    return create(
        ownerUserId,
        title,
        posterImageUrl,
        subtitle,
        content,
        location,
        qnaAccount,
        note,
        organization,
        department,
        contract,
        displayType,
        displayFields,
        DisplayRegion.OTHERS,
        period,
        artworkContentOpen,
        exhibitionContentOpen);
  }

  public static Display create(
      UserId ownerUserId,
      String title,
      String posterImageUrl,
      String subtitle,
      String content,
      DisplayLocation location,
      String qnaAccount,
      String note,
      String organization,
      String department,
      DisplayType displayType,
      List<DisplayField> displayFields,
      DisplayRegion region,
      DisplayPeriod period,
      ContentOpenPolicy artworkContentOpen,
      ContentOpenPolicy exhibitionContentOpen) {
    return create(
        ownerUserId,
        title,
        posterImageUrl,
        subtitle,
        content,
        location,
        qnaAccount,
        note,
        organization,
        department,
        null,
        displayType,
        displayFields,
        region,
        period,
        artworkContentOpen,
        exhibitionContentOpen);
  }

  public static Display create(
      UserId ownerUserId,
      String title,
      String posterImageUrl,
      String subtitle,
      String content,
      DisplayLocation location,
      String qnaAccount,
      String note,
      String organization,
      String department,
      String contract,
      DisplayType displayType,
      List<DisplayField> displayFields,
      DisplayRegion region,
      DisplayPeriod period,
      ContentOpenPolicy artworkContentOpen,
      ContentOpenPolicy exhibitionContentOpen) {
    return new Display(
            null,
            ownerUserId,
            title,
            subtitle,
            content,
            location,
            qnaAccount,
            note,
            organization,
            department,
            contract,
            displayType,
            period,
            artworkContentOpen,
            exhibitionContentOpen,
            DisplayStatus.DRAFT,
            null,
            null,
            List.of(
                new DisplayImage(
                    null,
                    posterImageUrl,
                    DisplayImageType.MAIN,
                    DEFAULT_MAIN_IMAGE_WIDTH,
                    DEFAULT_MAIN_IMAGE_HEIGHT,
                    MAIN_IMAGE_SORT_ORDER,
                    null)),
            List.of(),
            toFieldSelections(displayFields),
            List.of(),
            List.of())
        .withRegion(region);
  }

  public Display(
      Long id,
      UserId ownerUserId,
      String title,
      String subtitle,
      String content,
      DisplayLocation location,
      String qnaAccount,
      String note,
      String organization,
      String department,
      String contract,
      DisplayType displayType,
      DisplayPeriod period,
      ContentOpenPolicy artworkContentOpen,
      ContentOpenPolicy exhibitionContentOpen,
      DisplayStatus status,
      String invitationToken,
      LocalDateTime invitationDisabledAt,
      List<DisplayImage> images,
      List<DisplayContentCategory> contentCategories,
      List<DisplayFieldSelection> fieldSelections,
      List<TeamMember> teamMembers,
      List<DisplayInvitation> invitations) {
    this.id = id;
    this.ownerUserId = Objects.requireNonNull(ownerUserId, "ownerUserId must not be null.");
    changeBasicInfo(title, subtitle, content, qnaAccount, note, organization, department);
    changeContract(contract);
    changeLocation(location);
    changeClassification(displayType);
    changePeriod(period);
    changeRegion(DisplayRegion.OTHERS);
    changeOpenPolicy(artworkContentOpen, exhibitionContentOpen);
    this.status = Objects.requireNonNullElse(status, DisplayStatus.DRAFT);
    this.invitationToken = invitationToken;
    this.invitationDisabledAt = invitationDisabledAt;
    addAll(this::addImage, images);
    addAll(this::addContentCategory, contentCategories);
    addAll(this::addFieldSelection, fieldSelections);
    addAll(this::addTeamMember, teamMembers);
    addAll(this::addInvitation, invitations);
  }

  // 내부 이미지 목록을 외부에서 직접 수정하지 못하도록 읽기 전용으로 반환한다.
  public List<DisplayImage> getImages() {
    return Collections.unmodifiableList(images);
  }

  // 전시 대표(포스터) 이미지 URL을 반환한다. 등록된 MAIN 이미지가 없으면 null이다.
  public String getPosterImageUrl() {
    return images.stream()
        .filter(image -> image.getImageType() == DisplayImageType.MAIN)
        .filter(image -> !image.isDeleted())
        .map(DisplayImage::getImageUrl)
        .findFirst()
        .orElse(null);
  }

  // 소개 콘텐츠 카테고리 목록을 읽기 전용으로 반환한다.
  public List<DisplayContentCategory> getContentCategories() {
    return Collections.unmodifiableList(contentCategories);
  }

  // 전시에 선택된 전체 분야 목록을 읽기 전용으로 반환한다.
  public List<DisplayFieldSelection> getFieldSelections() {
    return Collections.unmodifiableList(fieldSelections);
  }

  // 전시 팀원 목록을 읽기 전용으로 반환한다.
  public List<TeamMember> getTeamMembers() {
    return Collections.unmodifiableList(teamMembers);
  }

  // 전시 초대 목록을 읽기 전용으로 반환한다.
  public List<DisplayInvitation> getInvitations() {
    return Collections.unmodifiableList(invitations);
  }

  // 전시의 기본 소개 정보를 변경한다.
  public void changeBasicInfo(
      String title,
      String subtitle,
      String content,
      String qnaAccount,
      String note,
      String organization,
      String department) {
    this.title = requireNonBlank(title, "title");
    this.subtitle = nullToEmpty(subtitle);
    this.content = nullToEmpty(content);
    this.qnaAccount = nullToEmpty(qnaAccount);
    this.note = nullToEmpty(note);
    this.organization = requireNonBlank(organization, "organization");
    this.department = nullToEmpty(department);
  }

  public void changeContract(String contract) {
    this.contract = contract;
  }

  // 전시 장소와 좌표 정보를 변경한다.
  public void changeLocation(DisplayLocation location) {
    this.location = Objects.requireNonNull(location, "location must not be null.");
  }

  // 전시 유형을 변경한다.
  public void changeClassification(DisplayType displayType) {
    this.displayType = Objects.requireNonNull(displayType, "displayType must not be null.");
  }

  // 전시 기간과 운영 시간을 변경한다.
  public void changePeriod(DisplayPeriod period) {
    this.period = Objects.requireNonNull(period, "period must not be null.");
  }

  // 전시 지역을 변경한다.
  public void changeRegion(DisplayRegion region) {
    this.region = Objects.requireNonNull(region, "region must not be null.");
  }

  private Display withRegion(DisplayRegion region) {
    changeRegion(region);
    return this;
  }

  // 작품 콘텐츠와 전시 콘텐츠의 공개 시점 정책을 변경한다.
  public void changeOpenPolicy(
      ContentOpenPolicy artworkContentOpen, ContentOpenPolicy exhibitionContentOpen) {
    this.artworkContentOpen =
        Objects.requireNonNull(artworkContentOpen, "artworkContentOpen must not be null.");
    this.exhibitionContentOpen =
        Objects.requireNonNull(exhibitionContentOpen, "exhibitionContentOpen must not be null.");
  }

  // 전시를 사용자에게 공개 가능한 발행 상태로 전환한다.
  public void publish() {
    this.status = DisplayStatus.PUBLISHED;
  }

  // 전시를 다시 초안 상태로 전환한다.
  public void changeToDraft() {
    this.status = DisplayStatus.DRAFT;
  }

  // 초대 토큰 해시를 새로 발급하고 기존 비활성화 상태를 해제한다.
  public void issueInvitationToken(String invitationToken) {
    this.invitationToken = requireNonBlank(invitationToken, "invitationToken");
    this.invitationDisabledAt = null;
  }

  // 초대 링크가 발급된 경우에만 비활성화하며, 이미 비활성화된 링크는 기존 시각을 유지한다.
  public void disableInvitation(LocalDateTime disabledAt) {
    ensureInvitationIssued();
    if (invitationDisabledAt == null) {
      this.invitationDisabledAt =
          Objects.requireNonNull(disabledAt, "disabledAt must not be null.");
    }
  }

  // 초대 토큰이 존재하고 비활성화되지 않은 상태인지 확인한다.
  public boolean isInvitationActive() {
    return invitationToken != null && invitationDisabledAt == null;
  }

  // 초대 링크로 전시에 접근할 수 있는 상태인지 검증한다.
  public void validateInvitationAccessible() {
    if (invitationToken == null) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_INVITATION_NOT_ISSUED);
    }
    if (!isInvitationActive()) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_INVITATION_DISABLED);
    }
  }

  // 초대 링크가 아직 발급되지 않은 전시는 비활성화할 수 없다.
  private void ensureInvitationIssued() {
    if (invitationToken == null) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_INVITATION_NOT_ISSUED);
    }
  }

  // 전시에 이미지를 추가한다.
  public void addImage(DisplayImage image) {
    DisplayImage displayImage = Objects.requireNonNull(image, "image must not be null.");
    ensureActiveImageNotDuplicated(displayImage);
    displayImage.assignDisplay(this);
    images.add(displayImage);
  }

  private void ensureActiveImageNotDuplicated(DisplayImage targetImage) {
    if (targetImage.isDeleted()) {
      return;
    }
    boolean duplicated =
        images.stream()
            .filter(image -> !image.isDeleted())
            .anyMatch(
                image ->
                    image.getImageType() == targetImage.getImageType()
                        && image.getSortOrder() == targetImage.getSortOrder());
    if (duplicated) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_IMAGE_ALREADY_EXISTS);
    }
  }

  // 전시 이미지 목록에서 특정 이미지를 제거한다.
  public void removeImage(Long imageId) {
    images.removeIf(image -> image.getId().equals(imageId));
  }

  // 전시 소개 콘텐츠 카테고리를 추가한다.
  public void addContentCategory(DisplayContentCategory category) {
    DisplayContentCategory contentCategory =
        Objects.requireNonNull(category, "category must not be null.");
    contentCategory.assignDisplay(this);
    contentCategories.add(contentCategory);
  }

  public DisplayContentCategory createContentCategory(String name, String description) {
    DisplayContentCategory category =
        new DisplayContentCategory(
            null, name, description, nextContentCategorySortOrder(), List.of());
    addContentCategory(category);
    return category;
  }

  public DisplayContentCategory changeContentCategory(
      Long categoryId, String name, String description) {
    DisplayContentCategory category = findContentCategory(categoryId);
    category.changeInfo(name, description);
    return category;
  }

  // 전시 소개 콘텐츠 카테고리를 제거한다.
  public void removeContentCategory(Long categoryId) {
    DisplayContentCategory category = findContentCategory(categoryId);
    contentCategories.remove(category);
  }

  public DisplayContent createContent(Long categoryId, String imageUrl, int width, int height) {
    return createContent(categoryId, imageUrl, width, height, DisplayContentStatus.PUBLISHED);
  }

  public DisplayContent createContent(
      Long categoryId, String imageUrl, int width, int height, DisplayContentStatus status) {
    DisplayContentCategory category = findContentCategory(categoryId);
    return category.createContent(imageUrl, width, height, status);
  }

  public DisplayContent changeContent(
      Long categoryId, Long contentId, String imageUrl, int width, int height) {
    DisplayContentCategory category = findContentCategory(categoryId);
    return category.changeContent(contentId, imageUrl, width, height);
  }

  public void removeContent(Long categoryId, Long contentId) {
    DisplayContentCategory category = findContentCategory(categoryId);
    category.removeContent(contentId);
  }

  public List<DisplayContent> reorderContents(Long categoryId, List<Long> orderedContentIds) {
    DisplayContentCategory category = findContentCategory(categoryId);
    category.reorderContents(orderedContentIds);
    return category.getContents();
  }

  private DisplayContentCategory findContentCategory(Long categoryId) {
    return contentCategories.stream()
        .filter(category -> Objects.equals(category.getId(), categoryId))
        .findFirst()
        .orElseThrow(
            () -> new BusinessException(DisplayErrorCode.DISPLAY_CONTENT_CATEGORY_NOT_FOUND));
  }

  private int nextContentCategorySortOrder() {
    return contentCategories.stream()
            .mapToInt(DisplayContentCategory::getSortOrder)
            .max()
            .orElse(-1)
        + 1;
  }

  // 전시 분야를 추가한다.
  public void addFieldSelection(DisplayFieldSelection fieldSelection) {
    DisplayFieldSelection selection =
        Objects.requireNonNull(fieldSelection, "fieldSelection must not be null.");
    selection.assignDisplay(this);
    fieldSelections.add(selection);
  }

  public void changeDisplayFields(List<DisplayField> displayFields) {
    List<DisplayFieldSelection> targetSelections = toFieldSelections(displayFields);
    fieldSelections.removeIf(
        selection ->
            targetSelections.stream()
                .noneMatch(targetSelection -> targetSelection.getField() == selection.getField()));

    for (DisplayFieldSelection targetSelection : targetSelections) {
      DisplayFieldSelection existingSelection =
          fieldSelections.stream()
              .filter(selection -> selection.getField() == targetSelection.getField())
              .findFirst()
              .orElse(null);
      if (existingSelection == null) {
        addFieldSelection(targetSelection);
      } else {
        existingSelection.changeSortOrder(targetSelection.getSortOrder());
      }
    }
  }

  public void changePosterImageUrl(String posterImageUrl) {
    DisplayImage mainImage =
        images.stream()
            .filter(image -> image.getImageType() == DisplayImageType.MAIN)
            .filter(image -> !image.isDeleted())
            .findFirst()
            .orElse(null);

    if (mainImage == null) {
      addImage(
          new DisplayImage(
              null,
              posterImageUrl,
              DisplayImageType.MAIN,
              DEFAULT_MAIN_IMAGE_WIDTH,
              DEFAULT_MAIN_IMAGE_HEIGHT,
              MAIN_IMAGE_SORT_ORDER,
              null));
      return;
    }

    mainImage.changeImageUrl(posterImageUrl);
  }

  // 전시 팀원을 추가한다.
  public void addTeamMember(TeamMember teamMember) {
    TeamMember member = Objects.requireNonNull(teamMember, "teamMember must not be null.");
    member.assignDisplay(this);
    teamMembers.add(member);
  }

  // 전시 팀원을 제거한다.
  public void removeTeamMember(Long teamMemberId) {
    teamMembers.removeIf(teamMember -> teamMember.getId().equals(teamMemberId));
  }

  // 전시 초대 정보를 추가한다.
  public void addInvitation(DisplayInvitation invitation) {
    DisplayInvitation displayInvitation =
        Objects.requireNonNull(invitation, "invitation must not be null.");
    displayInvitation.assignDisplay(this);
    invitations.add(displayInvitation);
  }

  // 전시 초대 정보를 제거한다.
  public void removeInvitation(Long invitationId) {
    invitations.removeIf(invitation -> invitation.getId().equals(invitationId));
  }

  // 현재 전시가 발행 상태인지 확인한다.
  public boolean isPublished() {
    return status == DisplayStatus.PUBLISHED;
  }

  public boolean isTeamLeader(Long userId) {
    return teamMembers.stream()
        .anyMatch(
            teamMember ->
                teamMember.isAccepted()
                    && !teamMember.isDeleted()
                    && teamMember.getUserId().value().equals(userId)
                    && teamMember.getRole() == TeamMemberRole.TEAM_LEADER);
  }

  public boolean hasAcceptedTeamMember(Long userId) {
    return teamMembers.stream()
        .anyMatch(
            teamMember ->
                teamMember.isAccepted()
                    && !teamMember.isDeleted()
                    && teamMember.getUserId().value().equals(userId));
  }

  public boolean hasPendingInvitation(Long inviteeUserId) {
    return invitations.stream()
        .anyMatch(
            invitation ->
                invitation.isPending()
                    && invitation.getInviteeUserId().value().equals(inviteeUserId));
  }

  public TeamMember inviteeAsTeamMember(DisplayInvitation invitation, String displayNickname) {
    DisplayInvitation displayInvitation =
        Objects.requireNonNull(invitation, "invitation must not be null.");
    if (!Objects.equals(id, displayInvitation.getDisplay().getId())) {
      throw new IllegalArgumentException("invitation must belong to this display.");
    }
    if (displayInvitation.isDeleted()) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_INVITATION_ALREADY_REJECTED);
    }
    if (displayInvitation.getStatus() != DisplayInvitationStatus.ACCEPTED) {
      throw new BusinessException(DisplayErrorCode.INVALID_DISPLAY_INVITATION_STATUS);
    }
    TeamMember teamMember =
        new TeamMember(
            null,
            displayInvitation.getInviteeUserId(),
            displayNickname,
            TeamMemberRole.TEAM_MEM,
            true);
    addTeamMember(teamMember);
    return teamMember;
  }

  public boolean isOwner(Long userId) {
    return ownerUserId.value().equals(userId);
  }

  public boolean canInviteMember(Long userId) {
    return isOwner(userId) || isTeamLeader(userId);
  }

  public boolean hasTeamMember(Long userId) {
    return teamMembers.stream()
        .anyMatch(
            teamMember ->
                teamMember.getUserId().value().equals(userId)
                    && teamMember.isAccepted()
                    && !teamMember.isDeleted());
  }

  private static <T> void addAll(java.util.function.Consumer<T> target, List<T> source) {
    if (source != null) {
      source.forEach(target);
    }
  }

  private static List<DisplayFieldSelection> toFieldSelections(List<DisplayField> displayFields) {
    if (displayFields == null || displayFields.isEmpty()) {
      throw new IllegalArgumentException("displayFields must not be empty.");
    }

    List<DisplayFieldSelection> selections = new ArrayList<>();
    for (int index = 0; index < displayFields.size(); index++) {
      selections.add(new DisplayFieldSelection(null, displayFields.get(index), index));
    }
    return selections;
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank.");
    }
    return value;
  }

  private static String nullToEmpty(String value) {
    return value == null ? "" : value;
  }
}
