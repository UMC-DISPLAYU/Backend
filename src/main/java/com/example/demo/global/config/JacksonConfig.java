package com.example.demo.global.config;

import com.example.demo.domain.health.presentation.HealthController.HealthResponse;
import com.example.demo.global.response.ApiResponseBody;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfig {

  @Bean
  public JsonMapperBuilderCustomizer utcLocalDateTimeCustomizer() {
    SimpleModule module = new SimpleModule();
    module.addSerializer(LocalDateTime.class, new UtcApiLocalDateTimeSerializer());
    return builder -> builder.addModule(module);
  }

  private static final class UtcApiLocalDateTimeSerializer extends ValueSerializer<LocalDateTime> {

    @Override
    public ValueSerializer<?> createContextual(
        SerializationContext context, BeanProperty property) {
      if (property != null
          && property.getMember() != null
          && isUtcApiResponse(property.getMember().getDeclaringClass())) {
        return this;
      }
      return LocalDateTimeSerializer.INSTANCE.createContextual(context, property);
    }

    @Override
    public void serialize(
        LocalDateTime value, JsonGenerator generator, SerializationContext context)
        throws JacksonException {
      generator.writeString(value.toInstant(ZoneOffset.UTC).toString());
    }

    private static boolean isUtcApiResponse(Class<?> owner) {
      return owner.getPackageName().endsWith(".presentation.response")
          || owner == HealthResponse.class
          || owner == ApiResponseBody.Meta.class;
    }
  }
}
