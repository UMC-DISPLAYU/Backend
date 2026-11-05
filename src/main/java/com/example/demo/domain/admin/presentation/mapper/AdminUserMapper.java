package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.UserDetail;
import com.example.demo.domain.admin.application.result.UserPage;
import com.example.demo.domain.admin.application.result.UserSummary;
import com.example.demo.domain.admin.presentation.response.UserResponses;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminUserMapper {
  UserResponses.Page toResponse(UserPage result);

  UserResponses.Summary toResponse(UserSummary result);

  UserResponses.Detail toResponse(UserDetail result);

  UserResponses.History toResponse(UserDetail.History result);
}
