package com.example.demo.domain.artist.application.usecase;

import com.example.demo.domain.artist.application.result.ArtistProfileSummaryResult;
import java.util.List;

public interface GetArtistProfileSummariesByUserIdUseCase {

  List<ArtistProfileSummaryResult> getArtistProfileSummariesByUserId(List<Long> userIds);
}
