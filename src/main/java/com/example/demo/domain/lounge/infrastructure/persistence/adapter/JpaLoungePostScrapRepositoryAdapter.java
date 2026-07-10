package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.domain.entity.LoungePostScrap;
import com.example.demo.domain.lounge.domain.repository.LoungePostScrapRepository;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungePostScrapJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLoungePostScrapRepositoryAdapter implements LoungePostScrapRepository {

    private final SpringDataLoungePostScrapJpaRepository jpaRepository;

    public JpaLoungePostScrapRepositoryAdapter(SpringDataLoungePostScrapJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<LoungePostScrap> findByLoungePostIdAndUserId(Long loungePostId, UserId userId) {
        return jpaRepository.findByLoungePostIdAndUserId(loungePostId, userId);
    }

    @Override
    public LoungePostScrap save(LoungePostScrap loungePostScrap) {
        return jpaRepository.save(loungePostScrap);
    }

    @Override
    public void delete(LoungePostScrap loungePostScrap) {
        jpaRepository.delete(loungePostScrap);
    }

    @Override
    public long countByLoungePostId(Long loungePostId) {
        return jpaRepository.countByLoungePostId(loungePostId);
    }
}
