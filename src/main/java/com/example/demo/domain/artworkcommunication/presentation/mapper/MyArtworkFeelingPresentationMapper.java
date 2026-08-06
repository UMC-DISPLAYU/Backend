package com.example.demo.domain.artworkcommunication.presentation.mapper;

import com.example.demo.domain.artworkcommunication.application.query.ArtworkCursorCodec;
import com.example.demo.domain.artworkcommunication.application.query.ArtworkCursorCodec.DecodedCursor;
import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkFeelingsQuery;
import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkFeelingsQuery.Cursor;
import com.example.demo.domain.artworkcommunication.application.result.MyArtworkFeelingListResult;
import com.example.demo.domain.artworkcommunication.presentation.response.MyArtworkFeelingListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.MyArtworkFeelingListResponse.MyArtworkFeelingResponse;
import org.springframework.stereotype.Component;

@Component
public class MyArtworkFeelingPresentationMapper {

  public GetMyArtworkFeelingsQuery toQuery(Long userId, String cursor, int size) {
    return new GetMyArtworkFeelingsQuery(userId, parseCursor(cursor), size);
  }

  public MyArtworkFeelingListResponse toResponse(MyArtworkFeelingListResult result) {
    return new MyArtworkFeelingListResponse(
        result.feelings().stream()
            .map(
                feeling ->
                    new MyArtworkFeelingResponse(
                        feeling.artworkId(),
                        feeling.personalArtworkId(),
                        feeling.artworkName(),
                        feeling.content(),
                        feeling.createdAt()))
            .toList(),
        result.nextCursor(),
        result.size(),
        result.hasNext());
  }

  private Cursor parseCursor(String cursor) {
    DecodedCursor decoded = ArtworkCursorCodec.decode(cursor);
    return decoded == null
        ? null
        : new Cursor(decoded.createdAt(), decoded.sourceOrder(), decoded.itemId());
  }
}
