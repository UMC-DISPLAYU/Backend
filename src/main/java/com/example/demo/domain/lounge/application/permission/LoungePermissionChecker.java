package com.example.demo.domain.lounge.application.permission;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.error.LoungeErrorCode;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LoungePermissionChecker {

  private final UserRepository userRepository;

  public LoungePermissionChecker(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void requireCategoryAccess(LoungePostCategory category, Long userId) {
    if (!category.requiresArtistVerification()) {
      return;
    }
    if (userId == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    if (!isVerifiedArtist(userId)) {
      throw new BusinessException(LoungeErrorCode.LOUNGE_ARTIST_VERIFICATION_REQUIRED);
    }
  }

  public void requirePostWriter(LoungePost loungePost, Long userId) {
    if (!loungePost.isAuthoredBy(userId)) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }
  }

  public void requireCommentWriter(LoungeComment comment, Long userId) {
    if (!comment.getAuthorUserId().value().equals(userId)) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }
  }

  public List<LoungePostCategory> getAccessibleCategories(Long userId) {
    boolean verifiedArtist = isVerifiedArtist(userId);
    return Arrays.stream(LoungePostCategory.values())
        .filter(category -> verifiedArtist || !category.requiresArtistVerification())
        .toList();
  }

  private boolean isVerifiedArtist(Long userId) {
    return userId != null && userRepository.findById(userId).filter(User::isVerified).isPresent();
  }
}
