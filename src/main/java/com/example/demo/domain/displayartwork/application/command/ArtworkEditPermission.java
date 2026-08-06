package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * 전시 출품작을 수정·삭제할 수 있는지 판별한다.
 *
 * <p>전시 대표자이거나, 해당 작품에 연결된 작가/공동 작업자 중 디유 계정이 요청자와 일치하면 권한이 있다. 대리 등록도 이 규칙으로 함께 처리된다. 팀원을 선택해 대리
 * 등록하면 그 팀원의 계정이 Creator에 연결되므로 통과하고, 이름만 직접 입력한 경우에는 연결된 계정이 없으므로(userId가 null) 대표자만 남는다.
 */
@Component
public class ArtworkEditPermission {

  private final CreatorRepository creatorRepository;

  public ArtworkEditPermission(CreatorRepository creatorRepository) {
    this.creatorRepository = creatorRepository;
  }

  public boolean canEdit(Display display, Long artworkId, Long requesterUserId) {
    if (requesterUserId == null) {
      return false;
    }
    if (display.isOwner(requesterUserId) || display.isTeamLeader(requesterUserId)) {
      return true;
    }
    List<Creator> creators = creatorRepository.findByDisplayArtworkId(artworkId);
    return creators.stream()
        .map(Creator::getUserId)
        .anyMatch(userId -> Objects.equals(userId, requesterUserId));
  }
}
