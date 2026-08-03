package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.application.result.ReceivedArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.application.result.ReceivedArtworkQuestionListResult.ReceivedArtworkQuestionItemResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetReceivedArtworkQuestionsService {

  private static final int MAX_PAGE_SIZE = 50;

  private final ReceivedArtworkQuestionQueryRepository queryRepository;

  public ReceivedArtworkQuestionListResult getReceivedQuestions(
      GetReceivedArtworkQuestionsQuery query) {
    int pageSize = Math.min(Math.max(query.size(), 1), MAX_PAGE_SIZE);
    List<ReceivedArtworkQuestionQueryItem> fetched =
        queryRepository.findReceivedByUserIdAndAnswerStatusWithCursor(
            query.userId(), query.answerStatus(), query.cursor(), pageSize + 1);

    boolean hasNext = fetched.size() > pageSize;
    List<ReceivedArtworkQuestionQueryItem> page = hasNext ? fetched.subList(0, pageSize) : fetched;
    List<ReceivedArtworkQuestionItemResult> questions =
        page.stream()
            .map(
                item ->
                    new ReceivedArtworkQuestionItemResult(
                        item.questionId(),
                        item.personalQuestionId(),
                        item.artworkId(),
                        item.personalArtworkId(),
                        item.artworkName(),
                        item.content(),
                        item.isPublic(),
                        item.answerStatus(),
                        item.questionerId(),
                        item.questionerNickname(),
                        item.createdAt()))
            .toList();
    String nextCursor = hasNext && !page.isEmpty() ? encodeCursor(page.getLast()) : null;

    return new ReceivedArtworkQuestionListResult(questions, nextCursor, pageSize, hasNext);
  }

  private String encodeCursor(ReceivedArtworkQuestionQueryItem item) {
    return ArtworkCursorCodec.encode(item.createdAt(), item.sourceOrder(), item.itemId());
  }
}
