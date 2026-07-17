package com.example.demo.domain.archive.application.query;

import com.example.demo.domain.archive.application.result.ArchiveArtistCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveArtistResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.domain.repository.ArchiveArtistRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetArchivedArtistsService {

  private static final int MAX_PAGE_SIZE = 50;

  private final ArchiveArtistRepository archiveArtistRepository;

  public GetArchivedArtistsService(ArchiveArtistRepository archiveArtistRepository) {
    this.archiveArtistRepository = archiveArtistRepository;
  }

  @Transactional(readOnly = true)
  public ArchiveArtistCursorResult getArchivedArtists(Long userId, Long cursorId, int size) {
    int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    List<ArchiveArtist> fetched =
        archiveArtistRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            userId, cursorId, pageSize + 1);

    boolean hasNext = fetched.size() > pageSize;
    List<ArchiveArtist> artists = hasNext ? fetched.subList(0, pageSize) : fetched;

    List<ArchiveArtistResult> results = artists.stream().map(ArchiveArtistResult::from).toList();

    Long nextCursorId = hasNext ? results.get(results.size() - 1).archiveArtistId() : null;
    return new ArchiveArtistCursorResult(results, nextCursorId, pageSize, hasNext);
  }
}
