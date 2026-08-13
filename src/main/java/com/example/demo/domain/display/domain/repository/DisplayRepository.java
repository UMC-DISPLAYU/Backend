package com.example.demo.domain.display.domain.repository;

import com.example.demo.domain.display.domain.aggregate.Display;
import java.util.List;
import java.util.Optional;

public interface DisplayRepository {

  Optional<Display> findById(Long displayId);

  Optional<Display> findByIdWithOptimisticLock(Long displayId);

  Optional<Display> findByInvitationToken(String invitationTokenHash);

  List<Display> findCreatedDisplaysByUserId(Long userId);

  List<Display> findParticipatedDisplaysByUserId(Long userId);

  List<Display> findPublishedCreatedDisplaysByUserId(Long userId);

  List<Display> findPublishedParticipatedDisplaysByUserId(Long userId);

  List<Display> findAll();

  boolean existsByOwnerUserIdAndTitle(Long ownerUserId, String title);

  Display save(Display display);

  void flush();

  void delete(Display display);
}
