package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalArtworkFeelingReplyJpaRepository
    extends JpaRepository<PersonalArtworkFeelingReply, Long> {

  List<PersonalArtworkFeelingReply> findByPersonalFeelingIdInAndDeletedAtIsNullOrderByCreatedAtAsc(
      List<Long> personalFeelingIds);
}
