package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.displayartwork.application.result.DeleteDisplayArtworkResult;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteDisplayArtworkService {

  private final DisplayArtworkRepository displayArtworkRepository;

  public DeleteDisplayArtworkService(DisplayArtworkRepository displayArtworkRepository) {
    this.displayArtworkRepository = displayArtworkRepository;
  }

  @Transactional
  public DeleteDisplayArtworkResult delete(Long requesterUserId, Long artworkId) {
    DisplayArtwork artwork =
        displayArtworkRepository
            .findById(artworkId)
            .filter(a -> !a.isDeleted())
            .orElseThrow(
                () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));

    // 전시를 만든 소유자는 TeamMember(TEAM_LEADER)로 등록되지 않으므로 소유자 여부도 함께 확인한다.
    boolean isTeamLeader =
        artwork.getDisplay().isOwner(requesterUserId)
            || artwork.getDisplay().isTeamLeader(requesterUserId);
    boolean isRegistrant = artwork.getRegisteredByUserId().equals(requesterUserId);
    if (!isTeamLeader && !isRegistrant) {
      throw new BusinessException(DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_ACTION);
    }

    artwork.delete();

    return new DeleteDisplayArtworkResult(artworkId, "작품이 성공적으로 삭제되었습니다.");
  }
}
