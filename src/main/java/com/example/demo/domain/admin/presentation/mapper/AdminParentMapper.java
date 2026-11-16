package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.ParentDetail;
import com.example.demo.domain.admin.presentation.response.ParentResponses;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminParentMapper {
  ParentResponses.Detail toResponse(ParentDetail result);

  ParentResponses.Parent toResponse(ParentDetail.Parent result);

  ParentResponses.Attachment toResponse(ParentDetail.Attachment result);
}
