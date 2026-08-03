package com.example.demo.domain.displayartwork.domain.repository;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.PreviewFilterType;
import java.util.List;
import java.util.Optional;

public interface DisplayArtworkRepository {

  Optional<DisplayArtwork> findById(Long displayArtworkId);

  int countByDisplayId(Long displayId);

  Optional<Integer> findMaxWorkSortOrderByDisplayId(Long displayId);

  List<DisplayArtwork> findAllByDisplayId(Long displayId);

  List<DisplayArtwork> findPublishedByDisplayId(Long displayId);

  /** 대표 작가/공동 작업자 구분 없이 해당 유저가 참여한 출품작을 등록순으로 조회한다. */
  List<DisplayArtwork> findAllByParticipantUserId(Long userId);

  /** page(0부터)*size 오프셋에서 size+1개를 가져와 다음 페이지 존재 여부를 판단할 수 있게 한다. */
  List<DisplayArtwork> findPreview(
      PreviewFilterType type, ArtworkType field, String school, int page, int size);

  DisplayArtwork save(DisplayArtwork displayArtwork);
}
