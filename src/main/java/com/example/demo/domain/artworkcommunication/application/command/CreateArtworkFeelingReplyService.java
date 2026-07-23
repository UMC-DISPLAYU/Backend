package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingReplyResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateArtworkFeelingReplyService {

  private final ArtworkFeelingReplyRepository artworkFeelingReplyRepository;
  private final ArtworkFeelingRepository artworkFeelingRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final CreatorExistenceRepository creatorExistenceRepository;
  private final ArtworkFeelingValidator artworkFeelingValidator;

  public ArtworkFeelingReplyResult createFeelingReply(ArtworkFeelingReplyCommand command) {

    artworkFeelingValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkFeelingValidator.validateUserExists(command.userId());
    artworkFeelingValidator.validateContent(command.content());

    ArtworkFeeling artworkFeeling =
        artworkFeelingRepository
            .findById(command.feelingId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.FEELING_NOT_FOUND));

    artworkFeelingValidator.validateReplyTarget(artworkFeeling, command.displayArtworkId());

    ArtworkFeelingReply savedFeelingReply =
        artworkFeelingReplyRepository.save(
            ArtworkFeelingReply.create(command.feelingId(), command.userId(), command.content()));

    Optional<String> creatorName =
        creatorExistenceRepository.findCreatorNameByDisplayArtworkIdAndUserId(
            command.displayArtworkId(), command.userId());
    boolean isCreator = creatorName.isPresent();
    String nickname =
        creatorName.orElseGet(
            () ->
                userExistenceRepository
                    .findNicknameById(command.userId())
                    .orElseThrow(
                        () -> new BusinessException(ArtworkCommunicationErrorCode.USER_NOT_FOUND)));

    return new ArtworkFeelingReplyResult(
        savedFeelingReply.getFeelingReplyId(),
        savedFeelingReply.getCreatedAt(),
        savedFeelingReply.getContent(),
        savedFeelingReply.getFeelingId(),
        savedFeelingReply.getUserId(),
        nickname,
        isCreator);
  }
}
