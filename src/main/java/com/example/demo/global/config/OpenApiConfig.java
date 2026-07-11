package com.example.demo.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  private static final List<String> TAG_ORDER = swaggerTags().stream().map(Tag::getName).toList();

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().title("DisplayU Swagger").version("v1"))
        .tags(swaggerTags());
  }

  @Bean
  public OpenApiCustomizer sortOpenApi() {
    return openApi -> {
      openApi.setTags(swaggerTags());

      if (openApi.getPaths() == null) {
        return;
      }

      Paths sortedPaths = new Paths();
      openApi.getPaths().entrySet().stream()
          .sorted(
              Comparator.comparingInt(
                      (Map.Entry<String, PathItem> entry) -> tagOrder(entry.getValue()))
                  .thenComparing(Map.Entry::getKey))
          .forEach(entry -> sortedPaths.addPathItem(entry.getKey(), entry.getValue()));
      openApi.setPaths(sortedPaths);
    };
  }

  private static List<Tag> swaggerTags() {
    return List.of(
        new Tag().name("Health").description("서버 상태 확인 API"),
        new Tag().name("Display").description("전시 API"),
        new Tag().name("Lounge Post").description("라운지 게시글 API"),
        new Tag().name("Lounge Comment").description("라운지 댓글 API"));
  }

  private static int tagOrder(PathItem pathItem) {
    return pathItem.readOperations().stream()
        .flatMap(
            operation ->
                operation.getTags() == null ? Stream.empty() : operation.getTags().stream())
        .mapToInt(OpenApiConfig::tagOrder)
        .min()
        .orElse(TAG_ORDER.size());
  }

  private static int tagOrder(String tag) {
    int index = TAG_ORDER.indexOf(tag);
    return index >= 0 ? index : TAG_ORDER.size();
  }
}
