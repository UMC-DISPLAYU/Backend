package com.example.demo.domain.lounge.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.domain.lounge.domain.error.LoungeErrorCode;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LoungeAccessPolicyTest {

  private final UserRepository userRepository = mock(UserRepository.class);
  private final LoungeAccessPolicy policy = new LoungeAccessPolicy(userRepository);

  @Test
  void allowsPublicCategoryWithoutAuthentication() {
    assertThatCode(() -> policy.validateCategoryAccess(LoungePostCategory.DISPLAY_REVIEW, null))
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

    assertThatCode(() -> policy.validateCategoryAccess(LoungePostCategory.WORK_TIP, 1L))
        .doesNotThrowAnyException();
  }

  @Test
  void returnsOnlyPublicCategoriesForAnonymousUser() {
    assertThat(policy.getAccessibleCategories(null))
        .containsExactly(LoungePostCategory.DISPLAY_REVIEW, LoungePostCategory.SPACE_RENTAL);
  }

  private void assertAccessDenied(Long userId, Object errorCode) {
    assertThatThrownBy(
            () -> policy.validateCategoryAccess(LoungePostCategory.COLLABORATION, userId))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(errorCode));
  }
}
