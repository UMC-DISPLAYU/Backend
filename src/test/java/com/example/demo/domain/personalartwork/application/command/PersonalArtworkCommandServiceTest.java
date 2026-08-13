package com.example.demo.domain.personalartwork.application.command;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.personalartwork.application.permission.PersonalArtworkPermissionChecker;
import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkImage;
import com.example.demo.domain.personalartwork.domain.error.PersonalArtworkErrorCode;
import com.example.demo.domain.personalartwork.domain.repository.ArtistVerificationRepository;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import com.example.demo.domain.personalartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.personalartwork.domain.type.ArtworkType;
import com.example.demo.domain.personalartwork.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** 개인 작품 등록은 전시 출품작과 마찬가지로 작가 인증을 요구한다. 다만 이미 등록한 작품의 수정·삭제까지 막지는 않는다. */
class PersonalArtworkCommandServiceTest {

  private static final Long OWNER = 1L;
  private static final Long ARTWORK_ID = 10L;

  private final PersonalArtworkRepository personalArtworkRepository =
      mock(PersonalArtworkRepository.class);
  private final ArtistVerificationRepository artistVerificationRepository =
      mock(ArtistVerificationRepository.class);

  private final PersonalArtworkCommandService service =
      new PersonalArtworkCommandService(
          personalArtworkRepository,
          new PersonalArtworkPermissionChecker(artistVerificationRepository));

  @Test
  void verifiedArtistCanCreatePersonalArtwork() {
    when(artistVerificationRepository.isVerifiedArtist(OWNER)).thenReturn(true);
    when(personalArtworkRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    assertThatCode(() -> service.createPersonalArtwork(OWNER, contentCommand()))
        .doesNotThrowAnyException();

    verify(personalArtworkRepository).save(any());
  }

  @Test
  void unverifiedUserCannotCreatePersonalArtwork() {
    when(artistVerificationRepository.isVerifiedArtist(OWNER)).thenReturn(false);

    assertThatThrownBy(() -> service.createPersonalArtwork(OWNER, contentCommand()))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(PersonalArtworkErrorCode.NOT_VERIFIED_ARTIST);

    // 검증에서 막혔으므로 저장까지 가지 않아야 한다.
    verify(personalArtworkRepository, never()).save(any());
  }

  @Test
  void unverifiedOwnerCanStillUpdateOwnPersonalArtwork() {
    when(personalArtworkRepository.findById(ARTWORK_ID)).thenReturn(Optional.of(artwork()));
    when(artistVerificationRepository.isVerifiedArtist(OWNER)).thenReturn(false);

    // 인증이 해제돼도 이미 등록한 작품은 관리할 수 있어야 한다.
    assertThatCode(() -> service.updatePersonalArtwork(ARTWORK_ID, OWNER, contentCommand()))
        .doesNotThrowAnyException();
  }

  @Test
  void unverifiedOwnerCanStillDeleteOwnPersonalArtwork() {
    when(personalArtworkRepository.findById(ARTWORK_ID)).thenReturn(Optional.of(artwork()));
    when(artistVerificationRepository.isVerifiedArtist(OWNER)).thenReturn(false);

    assertThatCode(() -> service.deletePersonalArtwork(ARTWORK_ID, OWNER))
        .doesNotThrowAnyException();
  }

  private static PersonalArtworkContentCommand contentCommand() {
    return new PersonalArtworkContentCommand(
        "작은 정원",
        "개인 작업으로 제작한 설치 작품입니다.",
        List.of(ArtworkType.COMPLEX),
        2026,
        "Mixed media",
        "100 x 100 cm",
        "빛과 그림자의 변화",
        List.of(imageCommand()));
  }

  private static PersonalArtworkImageCommand imageCommand() {
    return new PersonalArtworkImageCommand(
        "https://cdn.displayu.com/personal-artworks/garden.png",
        true,
        ArtworkImageType.ARTWORK,
        1,
        "대표 이미지",
        1200,
        1600);
  }

  private static PersonalArtwork artwork() {
    return PersonalArtwork.create(
        new UserId(OWNER),
        "작은 정원",
        "content",
        List.of(ArtworkType.COMPLEX),
        2026,
        "Mixed media",
        "100 x 100 cm",
        "point",
        List.of(
            new PersonalArtworkImage(
                null,
                "https://cdn.displayu.com/personal-artworks/garden.png",
                true,
                ArtworkImageType.ARTWORK,
                1,
                "대표 이미지",
                1200,
                1600)));
  }
}
