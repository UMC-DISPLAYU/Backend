package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.UserStatusResult;
import com.example.demo.domain.admin.presentation.response.UserStatusResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminUserStatusMapper {
  UserStatusResponse toResponse(UserStatusResult result);
}
