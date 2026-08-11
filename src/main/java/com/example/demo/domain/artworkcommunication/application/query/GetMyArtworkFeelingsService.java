package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.application.result.MyArtworkFeelingListResult;
import com.example.demo.domain.artworkcommunication.application.result.MyArtworkFeelingListResult.MyArtworkFeelingItemResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyArtworkFeelingsService {

  private static final int MAX_PAGE_SIZE = 50;

  private final MyArtworkFeelingQueryRepository queryRepository;

  @Transactional(readOnly = true)
  public MyArtworkFeelingListResult getMyFeelings(GetMyArtworkFeelingsQuery query) {
    int pageSize = Math.min(Math.max(query.size(), 1), MAX_PAGE_SIZE);
    List<MyArtworkFeelingQueryItem> fetched =
        queryRepository.findByUserIdWithCursor(query.userId(), query.cursor(), pageSize + 1);

    boolean hasNext = fetched.size() > pageSize;
    List<MyArtworkFeelingQueryItem> page = hasNext ? fetched.subList(0, pageSize) : fetched;
    List<MyArtworkFeelingItemResult> feelings =
        page.stream()
            .map(
                item ->
                    new MyArtworkFeelingItemResult(
                        item.artworkId(),
                        item.personalArtworkId(),
                        item.artworkName(),
                        item.content(),
                        item.createdAt()))
            .toList();
    String nextCursor = hasNext && !page.isEmpty() ? encodeCursor(page.getLast()) : null;

    return new MyArtworkFeelingListResult(feelings, nextCursor, pageSize, hasNext);
  }

  private String encodeCursor(MyArtworkFeelingQueryItem item) {
    return ArtworkCursorCodec.encode(item.createdAt(), item.sourceOrder(), item.itemId());
  }
}
