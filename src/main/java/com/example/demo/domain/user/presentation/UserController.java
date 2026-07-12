package com.example.demo.domain.user.presentation;

import com.example.demo.domain.user.application.service.UserService;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import com.example.demo.domain.user.presentation.response.NicknameCheckResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.domain.user.presentation.docs.UserControllerDocs;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @Override
    @GetMapping("/nickname/check")
    public ApiResponseBody<NicknameCheckResponse> checkNickname(
            @RequestParam(value = "nickname", required = false) String nickname,
            HttpServletRequest httpRequest
    ) {

        // nickname 파라미터 자체가 없거나 빈 값이면 형식 오류가 아니라 누락
        if (nickname == null || nickname.isBlank()) {
            throw new UserException(UserErrorCode.MISSING_NICKNAME);
        }

        boolean isAvailable = userService.isNicknameAvailable(nickname);

        return ApiResponseBody.success(
                new NicknameCheckResponse(nickname, isAvailable),
                httpRequest
        );
    }
}
