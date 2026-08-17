package com.example.demo.domain.displayartwork.domain.repository;

import com.example.demo.domain.displayartwork.domain.entity.Creator;
import java.util.List;
import java.util.Optional;

public interface CreatorRepository {

  List<Creator> findByDisplayArtworkId(Long displayArtworkId);

  Optional<Creator> findLeaderByDisplayArtworkId(Long displayArtworkId);

  List<Creator> findLeadersByDisplayArtworkIds(List<Long> displayArtworkIds);

  Creator save(Creator creator);

  List<Creator> saveAll(List<Creator> creators);

  /**
   * 전시 작가명이 바뀔 때, 그 이름을 그대로 쓰고 있던 작품의 작가명을 함께 갱신한다.
   *
   * <p>작품 작가명은 등록 시 전시 작가명을 복사해 두고 이후 작품별로 바꿀 수 있다(공동작업·팀명 표기). 그래서 기존 이름과 같은 행만 갱신해, 작품별로 수정한 표기는
   * 그대로 둔다.
   *
   * @return 갱신된 행 수
   */
  int renameCreatorNamesInDisplay(Long displayId, Long userId, String previousName, String newName);

  void deleteAllByDisplayArtworkId(Long displayArtworkId);
}
