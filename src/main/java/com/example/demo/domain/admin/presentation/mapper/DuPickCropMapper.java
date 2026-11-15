package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.presentation.request.UpdateDuPickCropRequest;
import com.example.demo.domain.display.domain.vo.PosterCrop;
import org.springframework.stereotype.Component;

@Component
public class DuPickCropMapper {
  public PosterCrop toCrop(UpdateDuPickCropRequest request) {
    var crop = request.posterCrop();
    return crop == null ? null : new PosterCrop(crop.x(), crop.y(), crop.w(), crop.h());
  }
}
