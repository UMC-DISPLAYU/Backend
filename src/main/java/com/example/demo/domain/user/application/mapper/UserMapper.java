package com.example.demo.domain.user.application.mapper;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.command.SignupCommand;
import com.example.demo.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(SignupCommand command, SocialUserInfo socialUserInfo) {
        return User.builder()
                .provider(socialUserInfo.provider())
                .providerId(socialUserInfo.providerId())
                .name(socialUserInfo.name())
                .nickname(command.nickname().value())
                .socialEmail(socialUserInfo.socialEmail())
                .build();
    }
}
