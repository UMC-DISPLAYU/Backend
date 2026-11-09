package com.example.demo.domain.display.application.query;

import java.time.LocalDate;
import java.util.List;

public interface GraduationDisplayQueryRepository {

  List<ClosingSoonDisplayQueryResult> findRandomGraduationDisplays(int size, LocalDate today);
}
