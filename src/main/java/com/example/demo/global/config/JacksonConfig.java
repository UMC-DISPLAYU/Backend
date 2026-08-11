package com.example.demo.global.config;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfig {

  @Bean
  public JsonMapperBuilderCustomizer utcLocalDateTimeCustomizer() {
    SimpleModule module = new SimpleModule();
    module.addSerializer(
        LocalDateTime.class,
        new ValueSerializer<>() {
          @Override
          public void serialize(
              LocalDateTime value, JsonGenerator generator, SerializationContext context)
              throws JacksonException {
            generator.writeString(value.toInstant(ZoneOffset.UTC).toString());
          }
        });
    return builder -> builder.addModule(module);
  }
}
