package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalArtworkQuestionReplyJpaRepository
    extends JpaRepository<PersonalArtworkQuestionReply, Long> {

  List<PersonalArtworkQuestionReply>
      findByPersonalQuestionIdInAndDeletedAtIsNullOrderByCreatedAtAsc(
          List<Long> personalQuestionIds);
}
