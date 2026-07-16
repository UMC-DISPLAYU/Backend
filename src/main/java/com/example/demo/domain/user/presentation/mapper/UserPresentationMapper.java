package com.example.demo.domain.user.presentation.mapper;

import com.example.demo.domain.user.application.result.MyUserResult;
import com.example.demo.domain.user.presentation.response.MyUserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserPresentationMapper {

  public MyUserResponse toResponse(MyUserResult result) {
    return new MyUserResponse(
        result.id(),
        result.provider().name(),
        result.name(),
        result.nickname(),
        result.isVerified(),
        result.socialEmail(),
        result.schoolEmail());
  }
}
