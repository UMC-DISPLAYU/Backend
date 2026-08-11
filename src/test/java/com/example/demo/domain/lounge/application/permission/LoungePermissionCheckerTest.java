package com.example.demo.domain.lounge.application.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.error.LoungeErrorCode;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LoungePermissionCheckerTest {

  private final UserRepository userRepository = mock(UserRepository.class);
  private final LoungePermissionChecker permissionChecker =
      new LoungePermissionChecker(userRepository);

  @Test
  void allowsPublicCategoryWithoutAuthentication() {
    assertThatCode(
            () -> permissionChecker.requireCategoryAccess(LoungePostCategory.DISPLAY_REVIEW, null))
        .doesNotThrowAnyException();
    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsAnonymousUserForArtistCategory() {
    assertAccessDenied(null, GlobalErrorCode.UNAUTHORIZED);
  }

  @Test
  void rejectsUnverifiedUserForArtistCategory() {
    User user = mock(User.class);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    assertAccessDenied(1L, LoungeErrorCode.LOUNGE_ARTIST_VERIFICATION_REQUIRED);
  }

  @Test
  void allowsVerifiedUserForArtistCategory() {
    User user = mock(User.class);
    when(user.isVerified()).thenReturn(true);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    assertThatCode(() -> permissionChecker.requireCategoryAccess(LoungePostCategory.WORK_TIP, 1L))
        .doesNotThrowAnyException();
  }

  @Test
  void returnsOnlyPublicCategoriesForAnonymousUser() {
    assertThat(permissionChecker.getAccessibleCategories(null))
        .containsExactly(LoungePostCategory.DISPLAY_REVIEW, LoungePostCategory.SPACE_RENTAL);
  }

  @Test
  void rejectsNonPostWriter() {
    LoungePost post =
        LoungePost.create(new UserId(1L), "title", "content", LoungePostCategory.DISPLAY_REVIEW);

    assertForbidden(() -> permissionChecker.requirePostWriter(post, 2L));
  }

  @Test
  void rejectsNonCommentWriter() {
    LoungeComment comment = LoungeComment.createComment(1L, new UserId(1L), "content");

    assertForbidden(() -> permissionChecker.requireCommentWriter(comment, 2L));
  }

  private void assertAccessDenied(Long userId, Object errorCode) {
    assertThatThrownBy(
            () -> permissionChecker.requireCategoryAccess(LoungePostCategory.COLLABORATION, userId))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(errorCode));
  }

  private void assertForbidden(Runnable action) {
    assertThatThrownBy(action::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(GlobalErrorCode.FORBIDDEN));
  }
}
