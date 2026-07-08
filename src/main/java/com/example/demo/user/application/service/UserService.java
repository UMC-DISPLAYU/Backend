package com.example.demo.user.application.service;

import com.example.demo.user.application.auth.SocialUserInfo;
import com.example.demo.user.application.command.SignupCommand;
import com.example.demo.user.application.mapper.UserMapper;
import com.example.demo.user.application.result.SignupResult;
import com.example.demo.user.domain.entity.User;
import com.example.demo.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;


    public SignupResult signup(
            SignupCommand command,
            SocialUserInfo socialUserInfo
    ) {

        User user = userMapper.toUser(
                command,
                socialUserInfo
        );

        User savedUser =
                userRepository.save(user);


        return new SignupResult(
                savedUser,
                null,
                null
        );
    }
}
