package com.example.demo.domain.artist.application.service;

import com.example.demo.domain.artist.application.result.ArtistProfileResult;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;
import com.example.demo.domain.artist.domain.repository.AreaOfActivityRepository;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetArtistProfileService {

  private final ArtistProfileRepository artistProfileRepository;
  private final AreaOfActivityRepository areaOfActivityRepository;

  @Transactional(readOnly = true)
  public ArtistProfileResult getMine(Long userId) {
    return toResult(findProfile(userId, "등록된 작가 프로필이 없습니다."));
  }

  @Transactional(readOnly = true)
  public ArtistProfileResult getByUserId(Long userId) {
    return toResult(findProfile(userId, "해당 사용자의 작가 프로필이 존재하지 않습니다."));
  }

  private ArtistProfile findProfile(Long userId, String message) {
    return artistProfileRepository
        .findByUserId(userId)
        .orElseThrow(() -> new ArtistException(ArtistErrorCode.ARTIST_PROFILE_NOT_FOUND, message));
  }

  private ArtistProfileResult toResult(ArtistProfile profile) {
    return new ArtistProfileResult(
        profile.getProfileImageUrl(),
        profile.getArtistName(),
        profile.getIntroduction(),
        profile.getUnivName(),
        profile.getPortfolioUrl(),
        areaOfActivityRepository.findByArtistProfile(profile).stream()
            .map(area -> area.getField())
            .toList(),
        profile.getUser().isVerified());
  }
}
