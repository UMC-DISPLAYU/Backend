package com.example.demo.domain.archive.application.usecase;

import com.example.demo.domain.archive.application.result.ArtistSaverResult;
import java.util.Collection;
import java.util.List;

/** 다른 도메인이 "이 작가들을 저장한 사용자"를 알아야 할 때 쓰는 창구. */
public interface GetArtistSaversUseCase {

  List<ArtistSaverResult> getArtistSavers(Collection<Long> artistUserIds);
}
