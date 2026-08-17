package com.example.demo.domain.personalartwork.application.service;

import com.example.demo.domain.artist.application.result.ArtistProfileSummaryResult;
import com.example.demo.domain.artist.application.usecase.GetArtistProfileSummariesByUserIdUseCase;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import com.example.demo.domain.personalartwork.application.usecase.GetPersonalArtworkSummariesUseCase;
import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPersonalArtworkSummariesService implements GetPersonalArtworkSummariesUseCase {

  private final PersonalArtworkRepository personalArtworkRepository;
  // "이 userId의 작가명은 무엇인가" 일괄 조회
  private final GetArtistProfileSummariesByUserIdUseCase getArtistProfileSummariesByUserIdUseCase;

  public GetPersonalArtworkSummariesService(
      PersonalArtworkRepository personalArtworkRepository,
      GetArtistProfileSummariesByUserIdUseCase getArtistProfileSummariesByUserIdUseCase) {
    this.personalArtworkRepository = personalArtworkRepository;
    this.getArtistProfileSummariesByUserIdUseCase = getArtistProfileSummariesByUserIdUseCase;
  }

  @Override
  @Transactional(readOnly = true)
  public List<PersonalArtworkSummaryResult> getPersonalArtworkSummaries(
      List<Long> personalArtworkIds) {
    if (personalArtworkIds.isEmpty()) {
      return List.of();
    }
    List<PersonalArtwork> artworks =
        personalArtworkRepository.findAllByIdInAndDeletedAtIsNull(personalArtworkIds);
    Map<Long, String> artistNamesByUserId = findArtistNamesByUserId(artworks);
    return artworks.stream()
        .map(
            artwork ->
                PersonalArtworkSummaryResult.from(
                    artwork, artistNamesByUserId.get(artwork.getOwnerUserId().value())))
        .toList();
  }

  /**
   * 저장 목록에는 여러 작가의 작품이 섞일 수 있어, 작품마다 조회하면 페이지당 조회가 그만큼 늘어난다. userId를 모아 한 번에 가져온다.
   *
   * <p>작가 프로필이 없는 사용자는 결과에 없으므로 이름이 null이 된다. 개인 작품 상세와 같은 동작이다.
   */
  private Map<Long, String> findArtistNamesByUserId(List<PersonalArtwork> artworks) {
    List<Long> userIds =
        artworks.stream().map(artwork -> artwork.getOwnerUserId().value()).distinct().toList();
    if (userIds.isEmpty()) {
      return Map.of();
    }
    return getArtistProfileSummariesByUserIdUseCase
        .getArtistProfileSummariesByUserId(userIds)
        .stream()
        .collect(
            Collectors.toMap(
                ArtistProfileSummaryResult::userId, ArtistProfileSummaryResult::artistName));
  }
}
