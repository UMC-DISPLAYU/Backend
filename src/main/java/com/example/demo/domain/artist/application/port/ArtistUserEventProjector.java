package com.example.demo.domain.artist.application.port;

import com.example.demo.domain.user.contract.event.v1.UserSchoolVerifiedEvent;
import com.example.demo.domain.user.contract.event.v1.UserUniversityChangedEvent;
import com.example.demo.domain.user.contract.event.v1.UserWithdrawnEvent;

public interface ArtistUserEventProjector {

  // 학교 인증, 대학 변경, 탈퇴 상태를 Artist에 반영하기 위한 포트 정의

  void project(UserSchoolVerifiedEvent event);

  void project(UserUniversityChangedEvent event);

  void project(UserWithdrawnEvent event);
}
