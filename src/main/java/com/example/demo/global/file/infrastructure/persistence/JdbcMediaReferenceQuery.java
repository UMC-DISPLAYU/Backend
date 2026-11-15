package com.example.demo.global.file.infrastructure.persistence;

import com.example.demo.global.file.application.port.MediaReferenceQuery;
import com.example.demo.global.file.infrastructure.FileStorageProperties;
import com.example.demo.global.file.infrastructure.ManagedMediaUrls;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcMediaReferenceQuery implements MediaReferenceQuery {
  private static final List<UrlSource> SOURCES = sources();
  private final NamedParameterJdbcTemplate jdbc;
  private final FileStorageProperties properties;

  public JdbcMediaReferenceQuery(
      NamedParameterJdbcTemplate jdbc, FileStorageProperties properties) {
    this.jdbc = jdbc;
    this.properties = properties;
  }

  @Override
  public Set<String> findReferencedUrls(Set<String> candidates) {
    if (candidates.isEmpty()) {
      return Set.of();
    }
    ManagedMediaUrls urls = new ManagedMediaUrls(properties);
    Map<String, String> candidateKeys = new LinkedHashMap<>();
    candidates.forEach(
        url -> {
          String key = urls.ownedKey(url);
          if (key != null) {
            candidateKeys.put(url, key.toLowerCase(Locale.ROOT));
          }
        });
    if (candidateKeys.isEmpty()) {
      return Set.of();
    }
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    List<String> keys = candidateKeys.values().stream().distinct().toList();
    for (int index = 0; index < keys.size(); index++) {
      parameters.addValue("key" + index, "%" + escapeLike(keys.get(index)) + "%");
    }
    String sql =
        SOURCES.stream()
            .map(source -> source.select(keys.size()))
            .collect(java.util.stream.Collectors.joining(" UNION ALL "));
    Set<String> referencedKeys = new HashSet<>();
    jdbc.query(
        sql,
        parameters,
        resultSet -> {
          String key = urls.referencedKey(resultSet.getString("media_url"));
          if (key != null) {
            referencedKeys.add(key.toLowerCase(Locale.ROOT));
          }
        });
    Set<String> referencedUrls = new HashSet<>();
    candidateKeys.forEach(
        (url, key) -> {
          if (referencedKeys.contains(key)) {
            referencedUrls.add(url);
          }
        });
    return Set.copyOf(referencedUrls);
  }

  private static String escapeLike(String key) {
    return key.replace("!", "!!").replace("%", "!%").replace("_", "!_");
  }

  private static List<UrlSource> sources() {
    List<UrlSource> sources = new ArrayList<>();
    for (String column : List.of("imageUrl", "thumbnailUrl")) {
      sources.add(
          new UrlSource(
              "i.`" + column + "`",
              "`DisplayImage` i JOIN `Display` d ON d.`displayId` = i.`displayId`",
              "i.`deletedAt` IS NULL AND d.`deletedAt` IS NULL"));
      sources.add(
          new UrlSource(
              "i.`" + column + "`",
              "`ArtworkImage` i JOIN `DisplayArtwork` a ON a.`displayArtworkId` = i.`displayArtworkId` JOIN `Display` d ON d.`displayId` = a.`displayId`",
              "a.`deletedAt` IS NULL AND d.`deletedAt` IS NULL"));
    }
    for (String source :
        List.of(
            "User.profileImageUrl",
            "ArtistProfile.profileImageUrl",
            "ArtistProfile.portfolioUrl",
            "ArtistProfile.instagramUrl",
            "Display.instagramUrl",
            "Display.websiteUrl",
            "Column.columnImageUrl",
            "DisplayContent.imageUrl",
            "PersonalArtworkImage.imageUrl",
            "LoungePostImage.imageUrl",
            "LoungeCommentImage.imageUrl",
            "DisplayReviewImage.imageUrl",
            "DisplayReviewReplyImage.imageUrl",
            "ArtworkQuestionImage.imageUrl",
            "ArtworkQuestionReplyImage.imageUrl",
            "ArtworkFeelingImage.imageUrl",
            "ArtworkFeelingReplyImage.imageUrl",
            "PersonalArtworkQuestionImage.imageUrl",
            "PersonalArtworkQuestionReplyImage.imageUrl",
            "PersonalArtworkFeelingImage.imageUrl",
            "PersonalArtworkFeelingReplyImage.imageUrl")) {
      String[] names = source.split("\\.");
      sources.add(new UrlSource("`" + names[1] + "`", "`" + names[0] + "`", "1 = 1"));
    }
    return List.copyOf(sources);
  }

  private record UrlSource(String column, String from, String active) {
    String select(int keyCount) {
      List<String> matches = new ArrayList<>();
      for (int index = 0; index < keyCount; index++) {
        matches.add("LOWER(" + column + ") LIKE :key" + index + " ESCAPE '!'");
      }
      matches.add("LOCATE('%', " + column + ") > 0");
      matches.add("LOCATE(CHAR(92), " + column + ") > 0");
      matches.add("LOCATE('/./', " + column + ") > 0");
      matches.add("LOCATE('/../', " + column + ") > 0");
      matches.add(column + " LIKE '%//%//%'");
      return "SELECT "
          + column
          + " AS media_url FROM "
          + from
          + " WHERE "
          + active
          + " AND "
          + column
          + " IS NOT NULL AND ("
          + String.join(" OR ", matches)
          + ")";
    }
  }
}
