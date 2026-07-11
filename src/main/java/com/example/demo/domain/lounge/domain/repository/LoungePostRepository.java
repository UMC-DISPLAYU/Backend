package com.example.demo.domain.lounge.domain.repository;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import java.util.List;
import java.util.Optional;

public interface LoungePostRepository {
  Optional<LoungePost> findById(Long loungePostId);

  List<LoungePost> findAll();

  LoungePost save(LoungePost loungePost);

  void delete(LoungePost loungePost);
}
