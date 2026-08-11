package com.example.demo.domain.memo.application.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.memo.domain.error.MemoErrorCode;
import com.example.demo.global.error.BusinessException;
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
    assertErrorCode(
        () -> checker.requireArchiveOwner(ArchiveDisplay.create(10L, OWNER_ID), OTHER_USER_ID),
        MemoErrorCode.ARCHIVE_DISPLAY_NOT_FOUND);
  }

  @Test
  void requireArchiveOwnerRejectsNonOwnerForWork() {
    assertErrorCode(
        () -> checker.requireArchiveOwner(ArchiveWork.create(10L, OWNER_ID), OTHER_USER_ID),
        MemoErrorCode.ARCHIVE_WORK_NOT_FOUND);
  }

  private void assertErrorCode(Runnable action, MemoErrorCode errorCode) {
    assertThatThrownBy(action::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(errorCode));
  }
}
