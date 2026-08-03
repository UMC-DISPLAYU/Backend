package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.application.result.DisplayDetailResult;
import java.util.Optional;

public interface DisplayDetailQueryRepository {

  Optional<DisplayDetailResult> findDisplayDetail(Long displayId, Long requesterUserId);
}
