package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkFeelingReplyJpaRepository extends JpaRepository<ArtworkFeelingReply, Long> {

  Optional<ArtworkFeelingReply> findFirstByFeelingIdAndDeletedAtIsNullOrderByCreatedAtAsc(
      Long feelingId);

  List<ArtworkFeelingReply> findByFeelingIdInAndDeletedAtIsNullOrderByCreatedAtAsc(
      List<Long> feelingIds);
}
