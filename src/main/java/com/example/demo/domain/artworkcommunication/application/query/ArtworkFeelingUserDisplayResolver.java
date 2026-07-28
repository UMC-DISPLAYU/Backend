package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.global.error.BusinessException;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ArtworkFeelingUserDisplayResolver {

  public UserDisplayInfo resolve(
      Long userId, Map<Long, String> nicknameByUserId, Map<Long, String> creatorNameByUserId) {
    String creatorName = creatorNameByUserId.get(userId);
    if (creatorName != null) {
      return new UserDisplayInfo(userId, creatorName, true);
    }

    String nickname = nicknameByUserId.get(userId);
    if (nickname == null) {
      throw new BusinessException(ArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
    return new UserDisplayInfo(userId, nickname, false);
  }

  public record UserDisplayInfo(Long userId, String nickname, boolean isCreator) {}
}
