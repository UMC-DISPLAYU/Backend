package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.ContentDetail;
import com.example.demo.domain.admin.application.result.ContentPage;
import com.example.demo.domain.admin.application.result.ContentSummary;
import com.example.demo.domain.admin.presentation.response.ContentResponses;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminContentMapper {
  ContentResponses.Page toResponse(ContentPage result);

  ContentResponses.Summary toResponse(ContentSummary result);

  ContentResponses.Detail toResponse(ContentDetail result);

  ContentResponses.History toResponse(ContentDetail.History result);
}
