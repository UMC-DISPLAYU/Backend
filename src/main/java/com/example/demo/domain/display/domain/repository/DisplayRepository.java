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

  List<Display> findAll();

  Display save(Display display);

  void flush();

  void delete(Display display);
}
