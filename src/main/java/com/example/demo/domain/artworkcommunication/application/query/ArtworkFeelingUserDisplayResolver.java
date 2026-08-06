package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository.UserProfile;
import com.example.demo.global.error.BusinessException;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ArtworkFeelingUserDisplayResolver {

  public UserDisplayInfo resolve(
      Long userId, Map<Long, UserProfile> userProfileById, Map<Long, String> creatorNameByUserId) {
    UserProfile userProfile = userProfileById.get(userId);
    if (userProfile == null) {
      throw new BusinessException(ArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }

    String creatorName = creatorNameByUserId.get(userId);
    if (creatorName != null) {
      return new UserDisplayInfo(userId, creatorName, userProfile.profileImageUrl(), true);
    }

    return new UserDisplayInfo(
        userId, userProfile.nickname(), userProfile.profileImageUrl(), false);
  }

  public record UserDisplayInfo(
      Long userId, String nickname, String profileImageUrl, boolean isCreator) {}
}
