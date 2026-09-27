package com.example.demo.domain.user.application.port;

import com.example.demo.domain.artist.contract.event.v1.ArtistProfileCreatedEvent;
import com.example.demo.domain.artist.contract.event.v1.ArtistQualificationChangedEvent;

public interface UserArtistEventProjector {

  // Artist 프로필 생성과 자격 변경을 User 호환 상태에 반영하기 위한 포트 정의
  void project(ArtistProfileCreatedEvent event);

  void project(ArtistQualificationChangedEvent event);
}
