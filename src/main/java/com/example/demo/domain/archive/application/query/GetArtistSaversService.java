package com.example.demo.domain.archive.application.query;

import com.example.demo.domain.archive.application.result.ArtistSaverResult;
import com.example.demo.domain.archive.application.usecase.GetArtistSaversUseCase;
import com.example.demo.domain.archive.domain.repository.ArchiveArtistRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetArtistSaversService implements GetArtistSaversUseCase {

  private final ArchiveArtistRepository archiveArtistRepository;

  public GetArtistSaversService(ArchiveArtistRepository archiveArtistRepository) {
    this.archiveArtistRepository = archiveArtistRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ArtistSaverResult> getArtistSavers(Collection<Long> artistUserIds) {
    return archiveArtistRepository.findActiveByArtistUserIds(artistUserIds).stream()
        .map(
            saved ->
                new ArtistSaverResult(
                    saved.getUserId(),
                    saved.getArtistUserId(),
                    saved.getArtistProfileId(),
                    saved.getSavedAt()))
        .toList();
  }
}
