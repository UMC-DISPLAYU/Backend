package com.example.demo.domain.user.presentation.mapper;

import com.example.demo.domain.user.application.command.ChangeNicknameCommand;
import com.example.demo.domain.user.application.command.UpdateMyProfileCommand;
import com.example.demo.domain.user.application.command.WithdrawUserCommand;
import com.example.demo.domain.user.application.result.ChangeNicknameResult;
import com.example.demo.domain.user.application.result.MyUserResult;
import com.example.demo.domain.user.application.result.UpdateMyProfileResult;
import com.example.demo.domain.user.application.result.UserSearchResult;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.domain.user.domain.vo.ProfileImageUrl;
import com.example.demo.domain.user.presentation.request.ChangeNicknameRequest;
import com.example.demo.domain.user.presentation.request.UpdateMyProfileRequest;
import com.example.demo.domain.user.presentation.response.ChangeNicknameResponse;
import com.example.demo.domain.user.presentation.response.MyUserResponse;
import com.example.demo.domain.user.presentation.response.UpdateMyProfileResponse;
import com.example.demo.domain.user.presentation.response.UserSearchResponse;
import org.springframework.stereotype.Component;

@Component
public class UserPresentationMapper {

  public WithdrawUserCommand toWithdrawCommand(Long userId) {
    return new WithdrawUserCommand(userId);
  }

  public ChangeNicknameCommand toCommand(Long userId, ChangeNicknameRequest request) {
    return new ChangeNicknameCommand(userId, Nickname.of(request.nickname()));
  }

  public ChangeNicknameResponse toResponse(ChangeNicknameResult result) {
    return new ChangeNicknameResponse(result.nickname());
  }

  public UpdateMyProfileCommand toCommand(Long userId, UpdateMyProfileRequest request) {
    return new UpdateMyProfileCommand(
        userId,
        ProfileImageUrl.ofNullable(request.profileImageUrl()),
        Nickname.of(request.nickname()));
  }

  public UpdateMyProfileResponse toResponse(UpdateMyProfileResult result) {
    return new UpdateMyProfileResponse(result.profileImageUrl(), result.nickname());
  }

  public MyUserResponse toResponse(MyUserResult result) {
    return new MyUserResponse(
        result.id(),
        result.provider().name(),
        result.name(),
        result.nickname(),
        result.profileImageUrl(),
        result.isVerified(),
        result.socialEmail(),
        result.schoolEmail());
  }

  public UserSearchResponse toResponse(UserSearchResult result) {
    return new UserSearchResponse(result.userId(), result.name(), result.nickname());
  }
}
