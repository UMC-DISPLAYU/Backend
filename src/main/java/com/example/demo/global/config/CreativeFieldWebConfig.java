package com.example.demo.global.config;

import com.example.demo.domain.artist.domain.type.ActivityCategory;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.global.domain.CreativeFieldCodes;
import java.util.function.Function;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class CreativeFieldWebConfig implements WebMvcConfigurer {
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(String.class, ActivityCategory.class, ActivityCategory::fromCode);
    registry.addConverter(String.class, DisplayField.class, DisplayField::fromCode);
    registry.addConverter(String.class, ArtworkType.class, ArtworkType::fromCode);
  }

  @Bean
  public JsonMapperBuilderCustomizer creativeFieldCustomizer() {
    SimpleModule module = new SimpleModule();
    register(module, ActivityCategory.class, ActivityCategory::fromCode);
    register(module, DisplayField.class, DisplayField::fromCode);
    register(module, ArtworkType.class, ArtworkType::fromCode);
    return builder -> builder.addModule(module);
  }

  private static <T extends Enum<T>> void register(
      SimpleModule module, Class<T> type, Function<String, T> parse) {
    module.addDeserializer(
        type,
        new ValueDeserializer<T>() {
          @Override
          public T deserialize(JsonParser parser, DeserializationContext context) {
            if (!parser.hasToken(JsonToken.VALUE_STRING)) {
              return context.reportInputMismatch(type, "분야 코드는 문자열이어야 합니다.");
            }
            String code = parser.getString();
            try {
              return parse.apply(code);
            } catch (IllegalArgumentException exception) {
              return context.reportInputMismatch(type, "지원하지 않는 분야 코드입니다: %s", code);
            }
          }
        });
    module.addSerializer(
        type,
        new ValueSerializer<T>() {
          @Override
          public void serialize(T value, JsonGenerator generator, SerializationContext context) {
            generator.writeString(CreativeFieldCodes.canonical(value.name()));
          }
        });
  }
}
