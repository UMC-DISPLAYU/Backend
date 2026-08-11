package com.example.demo.domain.personalartwork.application.usecase;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import java.util.List;

/**
 * 다른 도메인이 개인 작품 ID만 저장해 두고 작품 정보를 채워야 할 때 사용하는 조회 창구다.
 *
 * <p>소유자 기준 조회와 달리 작품마다 소유자가 다를 수 있어, ID 목록을 한 번에 넘겨 조회한다. 소프트 삭제된 작품은 결과에 포함되지 않으므로, 결과가 비어 있다는 것을
 * 작품이 존재하지 않는다는 판단 근거로 사용할 수 있다.
 */
public interface GetPersonalArtworkSummariesUseCase {

  List<PersonalArtworkSummaryResult> getPersonalArtworkSummaries(List<Long> personalArtworkIds);
}
