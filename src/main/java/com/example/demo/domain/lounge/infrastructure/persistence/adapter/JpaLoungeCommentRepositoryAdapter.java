package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentRepository;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungeCommentJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLoungeCommentRepositoryAdapter implements LoungeCommentRepository {

    private final SpringDataLoungeCommentJpaRepository jpaRepository;

    public JpaLoungeCommentRepositoryAdapter(SpringDataLoungeCommentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<LoungeComment> findById(Long loungeCommentId) {
        return jpaRepository.findById(loungeCommentId);
    }

    @Override
    public List<LoungeComment> findByLoungePostId(Long loungePostId) {
        return jpaRepository.findByLoungePostId(loungePostId);
    }

    @Override
    public LoungeComment save(LoungeComment loungeComment) {
        return jpaRepository.save(loungeComment);
    }

    @Override
    public void delete(LoungeComment loungeComment) {
        jpaRepository.delete(loungeComment);
    }
}
