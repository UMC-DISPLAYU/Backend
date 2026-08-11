package com.example.demo.domain.personalartwork.application.query;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkResult;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.error.PersonalArtworkErrorCode;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkLikeRepository;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonalArtworkQueryService {

  private final PersonalArtworkRepository personalArtworkRepository;
  private final PersonalArtworkLikeRepository personalArtworkLikeRepository;

  public PersonalArtworkQueryService(
      PersonalArtworkRepository personalArtworkRepository,
      PersonalArtworkLikeRepository personalArtworkLikeRepository) {
    this.personalArtworkRepository = personalArtworkRepository;
    this.personalArtworkLikeRepository = personalArtworkLikeRepository;
  }

  /**
   * 개인 작품 상세를 조회한다. 작가 프로필에서 타인의 작품도 열람할 수 있어야 하므로 소유자로 제한하지 않으며, 본인 소유 작품을 수정 화면에서 불러올 때도 같은 응답을
   * 사용한다.
   */
  @Transactional(readOnly = true)
  public PersonalArtworkResult getPersonalArtworkDetail(
      Long personalArtworkId, Long requesterUserId) {
    PersonalArtwork personalArtwork = getPersonalArtwork(personalArtworkId);
    long likeCount = personalArtworkLikeRepository.countByPersonalArtworkId(personalArtworkId);
    // 비회원 조회를 허용하므로 requesterUserId가 없으면 사용자별 상태는 false로 둔다.
    boolean isLiked =
        requesterUserId != null
            && personalArtworkLikeRepository
                .findByPersonalArtworkIdAndUserId(personalArtworkId, requesterUserId)
                .isPresent();
    return PersonalArtworkResult.from(personalArtwork, likeCount, isLiked);
  }

  private PersonalArtwork getPersonalArtwork(Long personalArtworkId) {
    return personalArtworkRepository
        .findById(personalArtworkId)
        .filter(artwork -> !artwork.isDeleted())
        .orElseThrow(
            () -> new BusinessException(PersonalArtworkErrorCode.PERSONAL_ARTWORK_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public List<PersonalArtworkSummaryResult> getPersonalArtworksByUser(Long userId) {
    return personalArtworkRepository.findAllByOwnerUserIdOrderByCreatedAtAsc(userId).stream()
        .map(PersonalArtworkSummaryResult::from)
        .toList();
  }
}
