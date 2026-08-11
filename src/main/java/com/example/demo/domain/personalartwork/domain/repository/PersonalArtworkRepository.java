package com.example.demo.domain.personalartwork.domain.repository;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import java.util.List;
import java.util.Optional;

public interface PersonalArtworkRepository {

  Optional<PersonalArtwork> findById(Long personalArtworkId);

  List<PersonalArtwork> findAllByOwnerUserIdOrderByCreatedAtAsc(Long userId);

  /**
   * 지정한 ID의 개인 작품을 한 번에 조회한다. 소유자 기준 조회와 달리 작품마다 소유자가 다를 수 있어, 다른 도메인이 저장해 둔 ID 목록으로 작품 정보를 채울 때
   * 사용한다. 소프트 삭제된 작품은 제외한다.
   */
  List<PersonalArtwork> findAllByIdInAndDeletedAtIsNull(List<Long> personalArtworkIds);

  PersonalArtwork save(PersonalArtwork personalArtwork);
}
