package com.example.demo.domain.lounge.domain.repository;

import com.example.demo.domain.lounge.domain.entity.LoungePostScrap;
import com.example.demo.domain.lounge.domain.vo.UserId;
import java.util.Optional;

public interface LoungePostScrapRepository {

    Optional<LoungePostScrap> findByLoungePostIdAndUserId(Long loungePostId, UserId userId);

    LoungePostScrap save(LoungePostScrap loungePostScrap);

    void delete(LoungePostScrap loungePostScrap);

    long countByLoungePostId(Long loungePostId);
}
