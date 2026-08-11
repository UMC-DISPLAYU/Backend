package com.example.demo.domain.displayartwork.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.application.permission.DisplayArtworkPermissionChecker;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.ArtistVerificationRepository;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.domain.displayartwork.domain.repository.UserNicknameRepository;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.global.error.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** 사용자 권한 정의 문서(DU-113) 기준으로 전시 작품 등록 권한 분기를 검증한다. */
class AuthorSetupServicePermissionTest {

  private static final Long ARTWORK_ID = 10L;
  private static final Long LEADER = 1L;
  private static final Long MEMBER = 2L;
  private static final Long OTHER_MEMBER = 3L;
  private static final Long OUTSIDER = 99L;

  private final DisplayArtworkRepository displayArtworkRepository =
      mock(DisplayArtworkRepository.class);
  private final CreatorRepository creatorRepository = mock(CreatorRepository.class);
  private final ArtistVerificationRepository artistVerificationRepository =
      mock(ArtistVerificationRepository.class);
  private final UserNicknameRepository userNicknameRepository = mock(UserNicknameRepository.class);
  private final DisplayArtworkPermissionChecker permissionChecker =
      new DisplayArtworkPermissionChecker(creatorRepository, artistVerificationRepository);

  private final AuthorSetupService service =
      new AuthorSetupService(
          displayArtworkRepository, creatorRepository, userNicknameRepository, permissionChecker);

  @Test
  void 전시_대표자는_계정이_없는_작가의_작품을_대리_등록하고_본인을_QA_담당자로_지정할_수_있다() {
    givenArtwork();
    when(artistVerificationRepository.isVerifiedArtist(anyLong())).thenReturn(true);

    service.setup(LEADER, command(null, "고상준", List.of(), List.of("공동작업자"), LEADER));

    // 대표 작가가 계정이 없으므로, Q&A 담당자인 대표자가 Creator로 함께 저장돼야 답변할 수 있다.
    assertThat(savedCreators())
        .extracting(
            Creator::getCreatorName, Creator::isLeader, Creator::isContact, Creator::getUserId)
        .containsExactly(
            tuple("고상준", true, false, null),
            tuple("공동작업자", false, false, null),
            tuple("대표자", false, true, LEADER));
  }

  @Test
  void 대표자가_아닌_팀원은_다른_팀원의_작품을_대신_등록할_수_없다() {
    givenArtwork();
    when(artistVerificationRepository.isVerifiedArtist(anyLong())).thenReturn(true);

    assertThatThrownBy(
            () ->
                service.setup(
                    MEMBER, command(OTHER_MEMBER, "다른 팀원", List.of(), List.of(), OTHER_MEMBER)))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.FORBIDDEN_PROXY_ARTWORK_REGISTRATION);
  }

  @Test
  void 대표자가_아닌_팀원은_계정이_없는_작가의_작품도_대신_등록할_수_없다() {
    givenArtwork();
    when(artistVerificationRepository.isVerifiedArtist(anyLong())).thenReturn(true);

    assertThatThrownBy(
            () -> service.setup(MEMBER, command(null, "외부 작가", List.of(), List.of(), MEMBER)))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.FORBIDDEN_PROXY_ARTWORK_REGISTRATION);
  }

  @Test
  void 인증된_팀원은_본인_작품을_등록할_수_있다() {
    givenArtwork();
    when(artistVerificationRepository.isVerifiedArtist(anyLong())).thenReturn(true);

    service.setup(MEMBER, command(MEMBER, "팀원", List.of(), List.of(), MEMBER));

    assertThat(savedCreators())
        .extracting(Creator::getUserId, Creator::isLeader, Creator::isContact)
        .containsExactly(tuple(MEMBER, true, true));
  }

  @Test
  void 전시에_속하지_않은_사용자는_QA_담당자가_될_수_없다() {
    givenArtwork();
    when(artistVerificationRepository.isVerifiedArtist(anyLong())).thenReturn(true);

    assertThatThrownBy(
            () -> service.setup(MEMBER, command(MEMBER, "팀원", List.of(), List.of(), OUTSIDER)))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.INVALID_QA_HANDLER);
  }

  @Test
  void 작가_인증을_받지_않은_팀원은_작품을_등록할_수_없다() {
    givenArtwork();
    when(artistVerificationRepository.isVerifiedArtist(MEMBER)).thenReturn(false);

    assertThatThrownBy(
            () -> service.setup(MEMBER, command(MEMBER, "팀원", List.of(), List.of(), MEMBER)))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.NOT_VERIFIED_ARTIST);
  }

  @Test
  void 작가_인증을_받지_않은_전시_대표자도_수정_시에는_작가_정보를_갱신할_수_있다() {
    // 작가 인증은 등록 조건이다. 인증 없이도 전시 대표자가 될 수 있어, 수정에서 요구하면 대표자가 수정하지 못한다.
    givenArtwork();
    when(artistVerificationRepository.isVerifiedArtist(LEADER)).thenReturn(false);
    when(artistVerificationRepository.isVerifiedArtist(MEMBER)).thenReturn(true);

    service.setupForUpdate(LEADER, command(MEMBER, "팀원", List.of(), List.of(), MEMBER));

    assertThat(savedCreators())
        .extracting(Creator::getUserId, Creator::isLeader)
        .containsExactly(tuple(MEMBER, true));
  }

  @Test
  void 작가_인증을_받지_않은_전시_대표자는_등록은_할_수_없다() {
    givenArtwork();
    when(artistVerificationRepository.isVerifiedArtist(LEADER)).thenReturn(false);

    assertThatThrownBy(
            () -> service.setup(LEADER, command(LEADER, "대표자", List.of(), List.of(), LEADER)))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.NOT_VERIFIED_ARTIST);
  }

  private void givenArtwork() {
    DisplayArtwork artwork = artwork();
    when(displayArtworkRepository.findById(ARTWORK_ID)).thenReturn(Optional.of(artwork));
  }

  @SuppressWarnings("unchecked")
  private List<Creator> savedCreators() {
    ArgumentCaptor<List<Creator>> captor = ArgumentCaptor.forClass(List.class);
    verify(creatorRepository).saveAll(captor.capture());
    return captor.getValue();
  }

  private static AuthorSetupCommand command(
      Long artistUserId,
      String artistName,
      List<Long> coAuthorUserIds,
      List<String> coAuthorRawNames,
      Long qaHandlerUserId) {
    return new AuthorSetupCommand(
        ARTWORK_ID,
        artistName,
        artistUserId,
        coAuthorUserIds,
        coAuthorRawNames,
        List.of(qaHandlerUserId));
  }

  private static DisplayArtwork artwork() {
    return DisplayArtwork.create(
        display(),
        "작품명",
        "작품 설명입니다.",
        ArtworkType.DESIGN,
        2026,
        "캔버스",
        "100x100",
        "감상 포인트",
        0,
        LEADER,
        List.of(
            new ArtworkImage(
                null,
                "https://cdn.displayu.com/artwork/main.jpg",
                true,
                ArtworkImageType.ARTWORK,
                0,
                null,
                1200,
                800)));
  }

  private static Display display() {
    Display display =
        Display.create(
            new UserId(LEADER),
            "FORM 2026",
            "https://cdn.displayu.com/posters/main.png",
            "subtitle",
            "content",
            new DisplayLocation("전시장", new BigDecimal("37.5513"), new BigDecimal("126.9248")),
            "",
            "",
            "organization",
            "department",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 5, 28),
                LocalDate.of(2026, 6, 5),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.IMMEDIATELY);
    display.addTeamMember(
        new TeamMember(11L, new UserId(LEADER), "대표자", TeamMemberRole.TEAM_LEADER, true));
    display.addTeamMember(
        new TeamMember(12L, new UserId(MEMBER), "팀원", TeamMemberRole.TEAM_MEM, true));
    display.addTeamMember(
        new TeamMember(13L, new UserId(OTHER_MEMBER), "다른 팀원", TeamMemberRole.TEAM_MEM, true));
    return display;
  }
}
