package com.example.demo.domain.notification.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class NotificationBodyConverterTest {

  private final NotificationBodyConverter converter = new NotificationBodyConverter();

  @Test
  void roundTripKeepsUserInputWithMarkupCharactersAsIs() {
    List<NotificationBodySegment> body =
        List.of(
            NotificationBodySegment.plain("“"),
            NotificationBodySegment.bold("별**빛 \"따옴표\" <b>태그</b>"),
            NotificationBodySegment.plain("” 작품이 삭제됐어요.\n복구할 수 없어요."));

    String json = converter.convertToDatabaseColumn(body);

    assertThat(converter.convertToEntityAttribute(json)).isEqualTo(body);
  }

  @Test
  void convertsNullToNullBothWays() {
    assertThat(converter.convertToDatabaseColumn(null)).isNull();
    assertThat(converter.convertToEntityAttribute(null)).isNull();
  }
}
