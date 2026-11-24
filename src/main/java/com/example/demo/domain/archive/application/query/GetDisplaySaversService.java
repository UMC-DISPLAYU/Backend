package com.example.demo.domain.archive.application.query;

import com.example.demo.domain.archive.application.usecase.GetDisplaySaversUseCase;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplaySaversService implements GetDisplaySaversUseCase {

  private final ArchiveDisplayRepository archiveDisplayRepository;

  public GetDisplaySaversService(ArchiveDisplayRepository archiveDisplayRepository) {
    this.archiveDisplayRepository = archiveDisplayRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Long> getSaverUserIds(Long displayId, LocalDateTime savedBefore) {
    return archiveDisplayRepository.findSaverUserIdsByDisplayIdSavedBefore(displayId, savedBefore);
  }
}
