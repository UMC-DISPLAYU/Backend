package com.example.demo.domain.display.application.usecase;

import com.example.demo.domain.display.application.result.GraduationDisplayResult;

public interface GetRandomGraduationDisplaysUseCase {

  GraduationDisplayResult getRandomGraduationDisplays(int size);
}
