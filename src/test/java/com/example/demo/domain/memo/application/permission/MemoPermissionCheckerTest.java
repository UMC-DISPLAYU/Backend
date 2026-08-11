package com.example.demo.domain.memo.application.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import org.junit.jupiter.api.Test;

class MemoPermissionCheckerTest {

  private static final Long OWNER_ID = 1L;
  private static final Long OTHER_USER_ID = 2L;

  private final MemoPermissionChecker checker = new MemoPermissionChecker();

  @Test
  void requireArchiveOwnerAllowsArchiveOwner() {
    ArchiveDisplay archiveDisplay = ArchiveDisplay.create(10L, OWNER_ID);

    assertThatCode(() -> checker.requireArchiveOwner(archiveDisplay, OWNER_ID))
        .doesNotThrowAnyException();
  }

  @Test
  void requireArchiveOwnerRejectsNonOwnerForDisplay() {
    assertForbidden(
        () -> checker.requireArchiveOwner(ArchiveDisplay.create(10L, OWNER_ID), OTHER_USER_ID));
  }

  @Test
  void requireArchiveOwnerRejectsNonOwnerForWork() {
    assertForbidden(
        () -> checker.requireArchiveOwner(ArchiveWork.create(10L, OWNER_ID), OTHER_USER_ID));
  }

  private void assertForbidden(Runnable action) {
    assertThatThrownBy(action::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(GlobalErrorCode.FORBIDDEN));
  }
}
