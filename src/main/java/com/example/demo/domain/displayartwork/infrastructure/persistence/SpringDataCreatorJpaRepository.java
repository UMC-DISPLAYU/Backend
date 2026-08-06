package com.example.demo.domain.displayartwork.infrastructure.persistence;

import com.example.demo.domain.displayartwork.domain.entity.Creator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataCreatorJpaRepository extends JpaRepository<Creator, Long> {

  List<Creator> findByDisplayArtworkId(Long displayArtworkId);

  Optional<Creator> findFirstByDisplayArtworkIdAndIsLeaderTrue(Long displayArtworkId);

  List<Creator> findByDisplayArtworkIdInAndIsLeaderTrue(List<Long> displayArtworkIds);

  /**
   * 작가 정보를 다시 저장하기 전에 기존 Creator를 지운다.
   *
   * <p>파생 삭제 메서드를 쓰면 Hibernate가 INSERT를 DELETE보다 먼저 flush해서, 같은 작가를 유지한 채 수정할 때 유니크
   * 제약(displayArtworkId, userId)에 걸린다. 그래서 즉시 실행되는 벌크 삭제를 사용한다. 뒤이어 Display의 팀원 정보를 계속 사용하므로 영속성
   * 컨텍스트는 비우지 않는다.
   */
  @Modifying(flushAutomatically = true)
  @Query("DELETE FROM Creator creator WHERE creator.displayArtworkId = :displayArtworkId")
  void deleteAllByDisplayArtworkId(@Param("displayArtworkId") Long displayArtworkId);
}
