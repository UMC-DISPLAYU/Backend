package com.example.demo.domain.display.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.MessageDigest;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;

class DisplayInvitationTokenGeneratorTest {

  @Test
  void generateCreatesUrlSafeTokenWithoutPadding() {
    DisplayInvitationTokenGenerator generator = new DisplayInvitationTokenGenerator();

    String token = generator.generate();

    assertThat(token).hasSize(43);
    assertThat(token).doesNotContain("+", "/", "=");
    assertThat(token).matches("[A-Za-z0-9_-]+");
  }

  @Test
  void hashUsesSha256Hex() throws Exception {
    DisplayInvitationTokenHasher hasher = new DisplayInvitationTokenHasher();

    String hash = hasher.hash("raw-token");

    byte[] expected = MessageDigest.getInstance("SHA-256").digest("raw-token".getBytes());
    assertThat(hash).isEqualTo(HexFormat.of().formatHex(expected));
    assertThat(hash).hasSize(64);
  }
}
