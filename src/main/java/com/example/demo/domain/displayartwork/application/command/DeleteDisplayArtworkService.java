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
  private final ArtworkEditPermission artworkEditPermission;

  public DeleteDisplayArtworkService(
      DisplayArtworkRepository displayArtworkRepository,
      ArtworkEditPermission artworkEditPermission) {
    this.displayArtworkRepository = displayArtworkRepository;
    this.artworkEditPermission = artworkEditPermission;
  }

  @Transactional
  public DeleteDisplayArtworkResult delete(Long requesterUserId, Long artworkId) {
    DisplayArtwork artwork =
        displayArtworkRepository
            .findById(artworkId)
            .filter(a -> !a.isDeleted())
            .orElseThrow(
                () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));

    // 전시 대표자, 작품의 작가, 공동 작업자만 삭제할 수 있다.
    if (!artworkEditPermission.canEdit(artwork.getDisplay(), artworkId, requesterUserId)) {
      throw new BusinessException(DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_ACTION);
    }

    artwork.delete();

    return new DeleteDisplayArtworkResult(artworkId, "작품이 성공적으로 삭제되었습니다.");
  }
}
