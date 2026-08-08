package com.example.demo.domain.lounge.application;

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
public class LoungeAccessPolicy {

  private final UserRepository userRepository;

  public LoungeAccessPolicy(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void validateCategoryAccess(LoungePostCategory category, Long userId) {
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
