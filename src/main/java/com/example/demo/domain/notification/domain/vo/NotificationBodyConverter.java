package com.example.demo.domain.notification.domain.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * 알림 내용 조각 목록을 JSON 문자열 하나로 저장한다. 조각은 항상 알림 단위로 통째로 읽고 쓰며 조각 하나로 조회할 일이 없어서 별도 테이블 대신 컬럼 하나에 담는다.
 */
@Converter
public class NotificationBodyConverter
    implements AttributeConverter<List<NotificationBodySegment>, String> {

  private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().build();
  private static final TypeReference<List<NotificationBodySegment>> SEGMENTS_TYPE =
      new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<NotificationBodySegment> segments) {
    if (segments == null) {
      return null;
    }
    try {
      return OBJECT_MAPPER.writeValueAsString(segments);
    } catch (JacksonException e) {
      throw new IllegalStateException("Failed to serialize notification body.", e);
    }
  }

  @Override
  public List<NotificationBodySegment> convertToEntityAttribute(String json) {
    if (json == null) {
      return null;
    }
    try {
      return OBJECT_MAPPER.readValue(json, SEGMENTS_TYPE);
    } catch (JacksonException e) {
      throw new IllegalStateException("Failed to deserialize notification body.", e);
    }
  }
}
