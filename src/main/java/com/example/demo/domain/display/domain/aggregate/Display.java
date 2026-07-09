package com.example.demo.domain.display.domain.aggregate;

import com.example.demo.domain.display.domain.entity.DisplayContentCategory;
import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayContentCategoryId;
import com.example.demo.domain.display.domain.vo.DisplayId;
import com.example.demo.domain.display.domain.vo.DisplayImageId;
import com.example.demo.domain.display.domain.vo.DisplayInvitationId;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.TeamMemberId;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

  // 식별자와 소유자 정보: 전시 Aggregate의 정체성과 생성/관리 주체를 나타낸다.
  @EmbeddedId
  @AttributeOverride(name = "value", column = @Column(name = "displayId"))
  private DisplayId id;

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

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DisplayField displayField;

  @Embedded private DisplayPeriod period;

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
  private final List<TeamMember> teamMembers = new ArrayList<>();

  @OneToMany(mappedBy = "display", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<DisplayInvitation> invitations = new ArrayList<>();

  protected Display() {}

  public Display(
      DisplayId id,
      UserId ownerUserId,
      String title,
      String subtitle,
      String content,
      DisplayLocation location,
      String qnaAccount,
      String note,
      String organization,
      String department,
      DisplayType displayType,
      DisplayField displayField,
      DisplayPeriod period,
      ContentOpenPolicy artworkContentOpen,
      ContentOpenPolicy exhibitionContentOpen,
      DisplayStatus status,
      String invitationToken,
      LocalDateTime invitationDisabledAt,
      List<DisplayImage> images,
      List<DisplayContentCategory> contentCategories,
      List<TeamMember> teamMembers,
      List<DisplayInvitation> invitations) {
    this.id = Objects.requireNonNull(id, "id must not be null.");
    this.ownerUserId = Objects.requireNonNull(ownerUserId, "ownerUserId must not be null.");
    changeBasicInfo(title, subtitle, content, qnaAccount, note, organization, department);
    changeLocation(location);
    changeClassification(displayType, displayField);
    changePeriod(period);
    changeOpenPolicy(artworkContentOpen, exhibitionContentOpen);
    this.status = Objects.requireNonNullElse(status, DisplayStatus.DRAFT);
    this.invitationToken = invitationToken;
    this.invitationDisabledAt = invitationDisabledAt;
    addAll(this::addImage, images);
    addAll(this::addContentCategory, contentCategories);
    addAll(this::addTeamMember, teamMembers);
    addAll(this::addInvitation, invitations);
  }

  // 내부 이미지 목록을 외부에서 직접 수정하지 못하도록 읽기 전용으로 반환한다.
  public List<DisplayImage> getImages() {
    return Collections.unmodifiableList(images);
  }

  // 소개 콘텐츠 카테고리 목록을 읽기 전용으로 반환한다.
  public List<DisplayContentCategory> getContentCategories() {
    return Collections.unmodifiableList(contentCategories);
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
    this.subtitle = requireNonBlank(subtitle, "subtitle");
    this.content = requireNonBlank(content, "content");
    this.qnaAccount = requireNonBlank(qnaAccount, "qnaAccount");
    this.note = requireNonBlank(note, "note");
    this.organization = requireNonBlank(organization, "organization");
    this.department = requireNonBlank(department, "department");
  }

  // 전시 장소와 좌표 정보를 변경한다.
  public void changeLocation(DisplayLocation location) {
    this.location = Objects.requireNonNull(location, "location must not be null.");
  }

  // 전시 유형과 분야를 변경한다.
  public void changeClassification(DisplayType displayType, DisplayField displayField) {
    this.displayType = Objects.requireNonNull(displayType, "displayType must not be null.");
    this.displayField = Objects.requireNonNull(displayField, "displayField must not be null.");
  }

  // 전시 기간과 운영 시간을 변경한다.
  public void changePeriod(DisplayPeriod period) {
    this.period = Objects.requireNonNull(period, "period must not be null.");
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

  // 팀원 초대 토큰을 변경하고 비활성화 상태를 해제한다.
  public void changeInvitationToken(String invitationToken) {
    this.invitationToken = requireNonBlank(invitationToken, "invitationToken");
    this.invitationDisabledAt = null;
  }

  // 현재 초대 토큰을 더 이상 사용할 수 없도록 비활성화한다.
  public void disableInvitation() {
    this.invitationDisabledAt = LocalDateTime.now();
  }

  // 전시에 이미지를 추가한다.
  public void addImage(DisplayImage image) {
    DisplayImage displayImage = Objects.requireNonNull(image, "image must not be null.");
    displayImage.assignDisplay(this);
    images.add(displayImage);
  }

  // 전시 이미지 목록에서 특정 이미지를 제거한다.
  public void removeImage(DisplayImageId imageId) {
    images.removeIf(image -> image.getId().equals(imageId));
  }

  // 전시 소개 콘텐츠 카테고리를 추가한다.
  public void addContentCategory(DisplayContentCategory category) {
    DisplayContentCategory contentCategory =
        Objects.requireNonNull(category, "category must not be null.");
    contentCategory.assignDisplay(this);
    contentCategories.add(contentCategory);
  }

  // 전시 소개 콘텐츠 카테고리를 제거한다.
  public void removeContentCategory(DisplayContentCategoryId categoryId) {
    contentCategories.removeIf(category -> category.getId().equals(categoryId));
  }

  // 전시 팀원을 추가한다.
  public void addTeamMember(TeamMember teamMember) {
    TeamMember member = Objects.requireNonNull(teamMember, "teamMember must not be null.");
    member.assignDisplay(this);
    teamMembers.add(member);
  }

  // 전시 팀원을 제거한다.
  public void removeTeamMember(TeamMemberId teamMemberId) {
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
  public void removeInvitation(DisplayInvitationId invitationId) {
    invitations.removeIf(invitation -> invitation.getId().equals(invitationId));
  }

  // 현재 전시가 발행 상태인지 확인한다.
  public boolean isPublished() {
    return status == DisplayStatus.PUBLISHED;
  }

  private static <T> void addAll(java.util.function.Consumer<T> target, List<T> source) {
    if (source != null) {
      source.forEach(target);
    }
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank.");
    }
    return value;
  }
}
