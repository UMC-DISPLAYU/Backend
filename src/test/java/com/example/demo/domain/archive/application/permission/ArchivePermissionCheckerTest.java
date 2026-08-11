package com.example.demo.domain.archive.application.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import org.junit.jupiter.api.Test;

class ArchivePermissionCheckerTest {

  private static final Long OWNER_ID = 1L;
  private static final Long OTHER_USER_ID = 2L;

  private final ArchivePermissionChecker checker = new ArchivePermissionChecker();

  @Test
  void requireOwnerAllowsArchiveOwner() {
    ArchiveDisplay archiveDisplay = ArchiveDisplay.create(10L, OWNER_ID);

    assertThatCode(() -> checker.requireOwner(archiveDisplay, OWNER_ID)).doesNotThrowAnyException();
  }

  @Test
  void requireOwnerRejectsNonOwnerForArtist() {
    assertForbidden(() -> checker.requireOwner(ArchiveArtist.create(10L, OWNER_ID), OTHER_USER_ID));
  }

  @Test
  void requireOwnerRejectsNonOwnerForDisplay() {
    assertForbidden(
        () -> checker.requireOwner(ArchiveDisplay.create(10L, OWNER_ID), OTHER_USER_ID));
  }

  @Test
  void requireOwnerRejectsNonOwnerForWork() {
    assertForbidden(() -> checker.requireOwner(ArchiveWork.create(10L, OWNER_ID), OTHER_USER_ID));
  }

  private void assertForbidden(Runnable action) {
    assertThatThrownBy(action::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(GlobalErrorCode.FORBIDDEN));
  }
}
