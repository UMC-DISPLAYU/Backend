package com.example.demo.domain.artist.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class ArtistServiceDependencyTest {

  @Test
  void artistWriteServicesDoNotDependOnUserEntityOrRepository() {
    assertNoDirectUserDependency(CreateArtistProfileService.class);
    assertNoDirectUserDependency(UpdateArtistProfileService.class);
  }

  private void assertNoDirectUserDependency(Class<?> serviceType) {
    assertThat(Arrays.stream(serviceType.getDeclaredFields()).map(Field::getType))
        .doesNotContain(User.class, UserRepository.class);
  }
}
