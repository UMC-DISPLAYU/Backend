package com.example.demo.domain.displayartwork.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.global.file.application.port.MediaStorageVerifier;
import com.example.demo.global.security.JwtFactory;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:creative_fields_test;MODE=MySQL;DATABASE_TO_UPPER=FALSE;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
      "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl",
      "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
      "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true",
      "spring.jpa.properties.hibernate.hbm2ddl.import_files=media-test-identities.sql"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CreativeFieldsControllerTest {
  @Autowired private MockMvc mvc;
  @Autowired private JwtFactory jwtFactory;
  @Autowired private EntityManager entityManager;
  @MockitoBean private MediaStorageVerifier storageVerifier;
  private Long ownerId;
  private Long displayId;

  @BeforeEach
  void fixture() {
    ownerId = 262L;
    entityManager
        .createNativeQuery(
            """
            INSERT INTO `User` (userId, createdAt, updatedAt, provider, providerId, role,
              name, nickname, socialEmail, isVerified, schoolEmail, univName)
            VALUES (:id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Google', 'artwork-media-owner',
              'USER', '미디어 작가', '미디어 작가', 'media@example.com', TRUE, 'artist@univ.ac.kr', '테스트대학교')
            """)
        .setParameter("id", ownerId)
        .executeUpdate();
    Display display =
        Display.create(
            new UserId(ownerId),
            "영상 전시",
            "https://cdn.example/display/poster.png",
            "subtitle",
            "content",
            new DisplayLocation("전시장", new BigDecimal("37.55"), new BigDecimal("126.92")),
            "",
            "",
            "organization",
            "department",
            DisplayType.GRADUATION,
            List.of(DisplayField.GRAPHIC),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.IMMEDIATELY);
    display.publish();
    entityManager.persist(display);
    entityManager.flush();
    displayId = display.getId();
    entityManager.clear();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "PAINTING",
        "SCULPTURE",
        "PHOTOGRAPHY",
        "GRAPHIC",
        "BRANDING",
        "PRODUCT",
        "VIDEO",
        "SPACE",
        "ARCHITECTURE",
        "UX_UI",
        "CRAFTS",
        "OTHERS"
      })
  void selectableCodesRoundTripAcrossArtistsAndArtworks(String code) throws Exception {
    artistCreate(code).andExpect(status().isCreated());
    for (String target : List.of("me", ownerId.toString())) {
      mvc.perform(
              get("/api/v1/artists/" + target + "/artist-profile")
                  .header(HttpHeaders.AUTHORIZATION, bearer()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success.data.fields[0]").value(code));
    }
    artistUpdate(code)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.fields[0]").value(code));
    create(code)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.types[0]").value(code));
    Long id = artworkId();
    entityManager.flush();
    entityManager.clear();
    for (String suffix : List.of("", "/edit")) {
      mvc.perform(
              get("/api/v1/artworks/" + id + suffix).header(HttpHeaders.AUTHORIZATION, bearer()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success.data.types[0]").value(code));
    }
  }

  @ParameterizedTest
  @CsvSource({"UXUI,UX_UI", "MEDIA,VIDEO", "CRAFT,CRAFTS", "ETC,OTHERS"})
  void aliasesNormalizeInJsonAndQueryWhileStoredAliasesRemainSearchable(
      String alias, String canonical) throws Exception {
    artistCreate(alias).andExpect(status().isCreated());
    mvc.perform(
            get("/api/v1/artists/me/artist-profile").header(HttpHeaders.AUTHORIZATION, bearer()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.fields[0]").value(canonical));
    create(alias)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.type").value(canonical));
    Long id = artworkId();
    entityManager.flush();
    entityManager
        .createNativeQuery("UPDATE ArtworkField SET field=:field WHERE displayArtworkId=:id")
        .setParameter("field", alias)
        .setParameter("id", id)
        .executeUpdate();
    entityManager
        .createNativeQuery("UPDATE DisplayArtwork SET type=:field WHERE displayArtworkId=:id")
        .setParameter("field", alias)
        .setParameter("id", id)
        .executeUpdate();
    entityManager.clear();
    for (String code : List.of(alias, canonical)) {
      mvc.perform(
              get("/api/v1/artworks/preview")
                  .param("type", "RECOMMEND")
                  .param("field", code)
                  .param("page", "0")
                  .param("size", "10"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success.data.artworks.length()").value(1));
    }
    mvc.perform(get("/api/v1/artworks/" + id + "/edit").header(HttpHeaders.AUTHORIZATION, bearer()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.type").value(canonical));
  }

  @Test
  void artworkAllowsThreeRejectsFourAndArtistStillAllowsOnlyTwo() throws Exception {
    create("PAINTING\",\"GRAPHIC\",\"BRANDING")
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.types.length()").value(3));
    create("PAINTING\",\"GRAPHIC\",\"BRANDING\",\"PRODUCT").andExpect(status().isBadRequest());
    artistCreate("PAINTING\",\"GRAPHIC\",\"BRANDING").andExpect(status().isBadRequest());
  }

  @Test
  void legacyCodesCanBeReadAndRetainedButNotNewlySelected() throws Exception {
    create("DESIGN").andExpect(status().isBadRequest());
    artistCreate("DESIGN").andExpect(status().isBadRequest());
    artistCreate("GRAPHIC").andExpect(status().isCreated());
    create("GRAPHIC").andExpect(status().isCreated());
    Long id = artworkId();
    entityManager.flush();
    entityManager.createNativeQuery("UPDATE AreaOfActivity SET field='DESIGN'").executeUpdate();
    entityManager
        .createNativeQuery("UPDATE ArtworkField SET field='DESIGN' WHERE displayArtworkId=:id")
        .setParameter("id", id)
        .executeUpdate();
    entityManager
        .createNativeQuery("UPDATE DisplayArtwork SET type='DESIGN' WHERE displayArtworkId=:id")
        .setParameter("id", id)
        .executeUpdate();
    entityManager.clear();
    artistUpdate("DESIGN")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.fields[0]").value("DESIGN"));
    mvc.perform(
            patch("/api/v1/artworks/" + id)
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(artworkBody("DESIGN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.types[0]").value("DESIGN"));
    artistUpdate("FASHION").andExpect(status().isBadRequest());
    mvc.perform(
            patch("/api/v1/artworks/" + id)
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(artworkBody("FASHION")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void aliasAndCanonicalDuplicatesDoNotSatisfyAnotherRequiredField() throws Exception {
    create("VIDEO").andExpect(status().isCreated());
    Long id = artworkId();
    entityManager.flush();
    entityManager
        .createNativeQuery(
            "INSERT INTO ArtworkField (displayArtworkId, field) VALUES (:id, 'MEDIA')")
        .setParameter("id", id)
        .executeUpdate();
    entityManager.clear();
    mvc.perform(
            get("/api/v1/artworks/preview")
                .param("type", "RECOMMEND")
                .param("field", "MEDIA", "PAINTING")
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks.length()").value(0));
    mvc.perform(
            get("/api/v1/artworks/preview")
                .param("type", "RECOMMEND")
                .param("field", "MEDIA", "VIDEO")
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks.length()").value(1));
  }

  private ResultActions artistCreate(String code) throws Exception {
    return mvc.perform(
        post("/api/v1/artists/me/artist-profile")
            .header(HttpHeaders.AUTHORIZATION, bearer())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"artistName\":\"테스트작가\",\"activityFields\":[\"" + code + "\"]}"));
  }

  private ResultActions artistUpdate(String code) throws Exception {
    return mvc.perform(
        patch("/api/v1/artists/me/artist-profile")
            .header(HttpHeaders.AUTHORIZATION, bearer())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"artistName\":\"테스트작가\",\"fields\":[\"" + code + "\"]}"));
  }

  private ResultActions create(String code) throws Exception {
    return mvc.perform(
        post("/api/v1/artworks")
            .header(HttpHeaders.AUTHORIZATION, bearer())
            .contentType(MediaType.APPLICATION_JSON)
            .content(artworkBody(code)));
  }

  private Long artworkId() {
    return entityManager
        .createQuery("select a.id from DisplayArtwork a where a.display.id=:id", Long.class)
        .setParameter("id", displayId)
        .getSingleResult();
  }

  private String bearer() {
    return "Bearer " + jwtFactory.create(ownerId.toString(), 3600000L, "ACCESS");
  }

  private String artworkBody(String code) {
    return """
    {"displayId":%d,"artworkName":"작품","types":["%s"],"productionYear":2026,"materialMedia":"재료",
    "images":[{"imageUrl":"https://cdn.example/artwork/image.png","imageType":"ARTWORK","isThumbnail":true,"sortOrder":0,"width":800,"height":600}],
    "artistName":"테스트작가","artistUserId":%d,"coAuthors":{"userIds":[],"rawNames":[]},"qaHandlerUserIds":[%d]}
    """
        .formatted(displayId, code, ownerId, ownerId);
  }
}
