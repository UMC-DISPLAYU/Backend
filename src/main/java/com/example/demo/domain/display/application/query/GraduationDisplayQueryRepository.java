package com.example.demo.domain.display.application.query;

import java.util.List;

public interface GraduationDisplayQueryRepository {

  List<ClosingSoonDisplayQueryResult> findRandomGraduationDisplays(int size);
}
