package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.permission.UserPermissionChecker;
import com.example.demo.domain.user.contract.command.ChangeUserUniversityCommand;
import com.example.demo.domain.user.contract.event.v1.UserUniversityChangedEvent;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.service.SchoolEmailValidator;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

class ChangeUserUniversityServiceTest {

  @Test
  void validatesChangesAndPublishesUniversityEvent() {
    UserRepository userRepository = mock(UserRepository.class);
    SchoolEmailValidator validator = mock(SchoolEmailValidator.class);
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    Clock clock = Clock.fixed(Instant.parse("2026-09-27T00:00:00Z"), ZoneOffset.UTC);
    ChangeUserUniversityService service =
        new ChangeUserUniversityService(
            userRepository, validator, new UserPermissionChecker(), eventPublisher, clock);
    User user = User.builder().id(1L).schoolEmail("student@du.ac.kr").univName("기존대학교").build();
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    var result = service.execute(new ChangeUserUniversityCommand(1L, " 변경대학교 "));

    assertThat(result.universityName()).isEqualTo("변경대학교");
    assertThat(user.getUnivName()).isEqualTo("변경대학교");
    verify(validator).validate("변경대학교", "student@du.ac.kr");
    ArgumentCaptor<UserUniversityChangedEvent> eventCaptor =
        ArgumentCaptor.forClass(UserUniversityChangedEvent.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());
    assertThat(eventCaptor.getValue().revision()).isEqualTo(1L);
  }
}
