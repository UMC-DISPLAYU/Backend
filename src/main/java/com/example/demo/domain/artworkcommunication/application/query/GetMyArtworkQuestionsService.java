package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.application.result.MyArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.application.result.MyArtworkQuestionListResult.MyArtworkQuestionItemResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyArtworkQuestionsService {

  private static final int MAX_PAGE_SIZE = 50;

  private final MyArtworkQuestionQueryRepository queryRepository;

  @Transactional(readOnly = true)
  public MyArtworkQuestionListResult getMyQuestions(GetMyArtworkQuestionsQuery query) {
    int pageSize = Math.min(Math.max(query.size(), 1), MAX_PAGE_SIZE);
    List<MyArtworkQuestionQueryItem> fetched =
        queryRepository.findByUserIdWithCursor(query.userId(), query.cursor(), pageSize + 1);

    boolean hasNext = fetched.size() > pageSize;
    List<MyArtworkQuestionQueryItem> page = hasNext ? fetched.subList(0, pageSize) : fetched;
    List<MyArtworkQuestionItemResult> questions =
        page.stream()
            .map(
                item ->
                    new MyArtworkQuestionItemResult(
                        item.questionId(),
                        item.personalQuestionId(),
                        item.artworkId(),
                        item.personalArtworkId(),
                        item.artworkName(),
                        item.content(),
                        item.isPublic(),
                        item.answerStatus(),
                        item.createdAt()))
            .toList();
    String nextCursor = hasNext && !page.isEmpty() ? encodeCursor(page.getLast()) : null;

    return new MyArtworkQuestionListResult(questions, nextCursor, pageSize, hasNext);
  }

  private String encodeCursor(MyArtworkQuestionQueryItem item) {
    return ArtworkCursorCodec.encode(item.createdAt(), item.sourceOrder(), item.itemId());
  }
}
