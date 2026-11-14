package com.example.demo.global.file.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import com.example.demo.global.file.infrastructure.FileStorageProperties;
import com.example.demo.global.file.infrastructure.S3UnusedMediaDeletionAdapter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import software.amazon.awssdk.services.s3.S3Client;

class JdbcMediaReferenceQueryTest {
  private static final String VIDEO = "https://cdn.example/videos/display/clip.mp4";
  private static final String THUMBNAIL = "https://cdn.example/images/display/cover.png";
  private static final String ARTWORK_VIDEO = "https://cdn.example/videos/artwork/clip.mp4";
  private static final String ARTWORK_THUMBNAIL = "https://cdn.example/images/artwork/cover.png";
  private static final FileStorageProperties PROPERTIES =
      new FileStorageProperties("region", "bucket", "https://cdn.example", 60L);
  private JdbcTemplate jdbc;
  private JdbcMediaReferenceQuery query;

  @BeforeEach
  void schema() {
    var dataSource =
        new DriverManagerDataSource(
            "jdbc:h2:mem:media_reference_" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1",
            "sa",
            "");
    jdbc = new JdbcTemplate(dataSource);
    query = new JdbcMediaReferenceQuery(new NamedParameterJdbcTemplate(dataSource), PROPERTIES);
    jdbc.execute(
        "CREATE TABLE `Display` (`displayId` BIGINT, `deletedAt` TIMESTAMP, `instagramUrl` VARCHAR, `websiteUrl` VARCHAR)");
    jdbc.execute(
        "CREATE TABLE `DisplayImage` (`displayId` BIGINT, `deletedAt` TIMESTAMP, `imageUrl` VARCHAR, `thumbnailUrl` VARCHAR)");
    jdbc.execute(
        "CREATE TABLE `DisplayArtwork` (`displayArtworkId` BIGINT, `displayId` BIGINT, `deletedAt` TIMESTAMP)");
    jdbc.execute(
        "CREATE TABLE `ArtworkImage` (`displayArtworkId` BIGINT, `imageUrl` VARCHAR, `thumbnailUrl` VARCHAR)");
    jdbc.execute("CREATE TABLE `User` (`profileImageUrl` VARCHAR)");
    jdbc.execute(
        "CREATE TABLE `ArtistProfile` (`profileImageUrl` VARCHAR, `portfolioUrl` VARCHAR, `instagramUrl` VARCHAR)");
    jdbc.execute("CREATE TABLE `Column` (`columnImageUrl` VARCHAR)");
    for (String table : imageTables()) {
      jdbc.execute("CREATE TABLE `" + table + "` (`imageUrl` VARCHAR)");
    }
    jdbc.update("INSERT INTO `Display` (`displayId`) VALUES (1)");
    jdbc.update("INSERT INTO `DisplayArtwork` (`displayArtworkId`, `displayId`) VALUES (10, 1)");
  }

  @AfterEach
  void closeDatabase() {
    jdbc.execute("SHUTDOWN");
  }

  @Test
  void protectsAllFourGalleryReferences() {
    jdbc.update(
        "INSERT INTO `DisplayImage` (`displayId`, `imageUrl`, `thumbnailUrl`) VALUES (1, ?, ?)",
        VIDEO,
        THUMBNAIL);
    jdbc.update(
        "INSERT INTO `ArtworkImage` (`displayArtworkId`, `imageUrl`, `thumbnailUrl`) VALUES (10, ?, ?)",
        ARTWORK_VIDEO,
        ARTWORK_THUMBNAIL);
    Set<String> candidates = Set.of(VIDEO, THUMBNAIL, ARTWORK_VIDEO, ARTWORK_THUMBNAIL);
    assertThat(query.findReferencedUrls(candidates))
        .containsExactlyInAnyOrderElementsOf(candidates);
  }

  @Test
  void deletedGalleryParentsDoNotKeepFilesAlive() {
    jdbc.update(
        "INSERT INTO `DisplayImage` (`displayId`, `imageUrl`, `thumbnailUrl`) VALUES (1, ?, ?)",
        VIDEO,
        THUMBNAIL);
    jdbc.update(
        "INSERT INTO `ArtworkImage` (`displayArtworkId`, `imageUrl`, `thumbnailUrl`) VALUES (10, ?, ?)",
        ARTWORK_VIDEO,
        ARTWORK_THUMBNAIL);
    jdbc.update("UPDATE `Display` SET `deletedAt` = CURRENT_TIMESTAMP");
    assertThat(query.findReferencedUrls(Set.of(VIDEO, THUMBNAIL, ARTWORK_VIDEO, ARTWORK_THUMBNAIL)))
        .isEmpty();
  }

  @Test
  void deletedImageAndArtworkDoNotKeepFilesAlive() {
    jdbc.update(
        "INSERT INTO `DisplayImage` (`displayId`, `deletedAt`, `imageUrl`) VALUES (1, CURRENT_TIMESTAMP, ?)",
        THUMBNAIL);
    jdbc.update(
        "INSERT INTO `ArtworkImage` (`displayArtworkId`, `imageUrl`) VALUES (10, ?)",
        ARTWORK_VIDEO);
    jdbc.update("UPDATE `DisplayArtwork` SET `deletedAt` = CURRENT_TIMESTAMP");
    assertThat(query.findReferencedUrls(Set.of(THUMBNAIL, ARTWORK_VIDEO))).isEmpty();
  }

  @Test
  void anotherActiveGalleryPreservesAFileFromDeletedArtwork() {
    jdbc.update(
        "INSERT INTO `ArtworkImage` (`displayArtworkId`, `imageUrl`) VALUES (10, ?)",
        ARTWORK_THUMBNAIL);
    jdbc.update("UPDATE `DisplayArtwork` SET `deletedAt` = CURRENT_TIMESTAMP");
    jdbc.update(
        "INSERT INTO `DisplayImage` (`displayId`, `imageUrl`) VALUES (1, ?)", ARTWORK_THUMBNAIL);
    assertThat(query.findReferencedUrls(Set.of(ARTWORK_THUMBNAIL)))
        .containsExactly(ARTWORK_THUMBNAIL);
  }

  @ParameterizedTest
  @MethodSource("otherReferences")
  void protectsReferencesOutsideExhibitionAndArtworkGalleries(String source) {
    String[] parts = source.split("\\.");
    jdbc.update("INSERT INTO `" + parts[0] + "` (`" + parts[1] + "`) VALUES (?)", THUMBNAIL);
    assertThat(query.findReferencedUrls(Set.of(VIDEO, THUMBNAIL))).containsExactly(THUMBNAIL);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "https://CDN.EXAMPLE/images/display/cover.png",
        "http://cdn.example:80/images/display/cover.png?token=private#preview",
        "https://cdn.example:443/images/display/cover.png",
        "//cdn.example/images/display/cover.png",
        "https://cdn.example/%69mages/display/cover.png",
        "https://cdn.example/images/./display/cover.png",
        "https://cdn.example/images/unused/../display/cover.png",
        "https://cdn.example/images//display/cover.png"
      })
  void protectsAliasesOfTheSameStoredObject(String alias) {
    jdbc.update("INSERT INTO `User` (`profileImageUrl`) VALUES (?)", alias);
    assertThat(query.findReferencedUrls(Set.of(THUMBNAIL))).containsExactly(THUMBNAIL);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "https:cdn.example/images/display/cover.png",
        "/images/display/cover.png",
        "https://cdn.example/images/display/%2563over.png",
        "https://cdn.example/images/display/%zz.png",
        "https://%63dn.example/images/display/cover.png",
        "https://%63dn.example/images/display/%zz.png",
        "https://cdn.example/../images/display/cover.png",
        "https://cdn.example/a/../../images/display/cover.png",
        "https://cdn.example/%2e%2e/images/display/cover.png"
      })
  void uncertainManagedReferencePreventsEveryS3Delete(String alias) {
    jdbc.update("INSERT INTO `User` (`profileImageUrl`) VALUES (?)", alias);
    S3Client s3 = mock(S3Client.class);
    var adapter = new S3UnusedMediaDeletionAdapter(s3, PROPERTIES, query);
    assertThatThrownBy(() -> adapter.deleteAll(List.of(VIDEO, THUMBNAIL)))
        .isInstanceOf(IllegalStateException.class);
    verifyNoInteractions(s3);
  }

  @Test
  void externalEncodedUrlDoesNotBlockOwnedFileCleanup() {
    jdbc.update(
        "INSERT INTO `User` (`profileImageUrl`) VALUES (?)",
        "https://outside.example/images/%2563over.png");
    assertThat(query.findReferencedUrls(Set.of(THUMBNAIL))).isEmpty();
  }

  private static Stream<String> otherReferences() {
    return Stream.concat(
        Stream.of(
            "User.profileImageUrl",
            "ArtistProfile.profileImageUrl",
            "ArtistProfile.portfolioUrl",
            "ArtistProfile.instagramUrl",
            "Display.instagramUrl",
            "Display.websiteUrl",
            "Column.columnImageUrl"),
        imageTables().stream().map(table -> table + ".imageUrl"));
  }

  private static List<String> imageTables() {
    return List.of(
        "DisplayContent",
        "PersonalArtworkImage",
        "LoungePostImage",
        "LoungeCommentImage",
        "DisplayReviewImage",
        "DisplayReviewReplyImage",
        "ArtworkQuestionImage",
        "ArtworkQuestionReplyImage",
        "ArtworkFeelingImage",
        "ArtworkFeelingReplyImage",
        "PersonalArtworkQuestionImage",
        "PersonalArtworkQuestionReplyImage",
        "PersonalArtworkFeelingImage",
        "PersonalArtworkFeelingReplyImage");
  }
}
