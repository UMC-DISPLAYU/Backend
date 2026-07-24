package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository.DisplayReviewAccess;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DisplayReviewAccessJpaAdapter implements DisplayReviewAccessRepository {
  private final DisplayExistenceJpaRepository displayRepository;
  private final DisplayReviewTeamMemberReferenceJpaRepository teamMemberRepository;
  private final DisplayReviewPersistenceMapper mapper;

  @Override
  public Optional<DisplayReviewAccess> findByDisplayId(Long displayId) {
    return displayRepository.findById(displayId).map(display -> mapper.toAccess(display, false));
  }

  @Override
  public Optional<DisplayReviewAccess> findByDisplayIdAndUserId(Long displayId, Long userId) {
    return displayRepository
        .findById(displayId)
        .map(
            display ->
                mapper.toAccess(
                    display,
                    teamMemberRepository.existsByDisplayIdAndUserIdAndAcceptedTrue(
                        displayId, userId)));
  }

  @Override
  public Set<Long> findAcceptedTeamMemberUserIds(Long displayId) {
    return teamMemberRepository.findAcceptedUserIdsByDisplayId(displayId);
  }
}
