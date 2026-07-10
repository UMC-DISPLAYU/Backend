package com.example.demo.domain.display.application.usecase;

import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult;

public interface GetRandomGraduationDisplaysUseCase {

  ClosingSoonDisplayResult getRandomGraduationDisplays(int size);
}
