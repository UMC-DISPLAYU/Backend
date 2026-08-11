package com.example.demo.domain.personalartworkcommunication.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

import com.example.demo.domain.personalartworkcommunication.application.command.DeletePersonalArtworkFeelingReplyService;
import com.example.demo.domain.personalartworkcommunication.application.command.DeletePersonalArtworkFeelingService;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkFeelingLikeService;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkFeelingReplyLikeService;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkFeelingReplyService;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkFeelingService;
import com.example.demo.domain.personalartworkcommunication.application.query.GetPersonalArtworkFeelingRepliesService;
import com.example.demo.domain.personalartworkcommunication.application.query.GetPersonalArtworkFeelingsService;
import com.example.demo.domain.personalartworkcommunication.presentation.mapper.PersonalArtworkFeelingPresentationMapper;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonalArtworkFeelingControllerValidationTest {

  private PersonalArtworkFeelingController controller;
  private ExecutableValidator executableValidator;
  private Method getFeelingReplies;

  @BeforeEach
  void setUp() throws NoSuchMethodException {
    controller =
        new PersonalArtworkFeelingController(
            mock(PersonalArtworkFeelingService.class),
            mock(DeletePersonalArtworkFeelingService.class),
            mock(DeletePersonalArtworkFeelingReplyService.class),
            mock(PersonalArtworkFeelingReplyService.class),
            mock(PersonalArtworkFeelingLikeService.class),
            mock(PersonalArtworkFeelingReplyLikeService.class),
            mock(GetPersonalArtworkFeelingsService.class),
            mock(GetPersonalArtworkFeelingRepliesService.class),
            mock(PersonalArtworkFeelingPresentationMapper.class));
    executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
    getFeelingReplies =
        PersonalArtworkFeelingController.class.getMethod(
            "getFeelingReplies",
            Long.class,
            Long.class,
            Long.class,
            int.class,
            AuthUser.class,
            HttpServletRequest.class);
  }

  @Test
  void inheritedParameterConstraintsDoNotConflict() {
    Object[] parameters = {1L, 1L, null, 10, null, mock(HttpServletRequest.class)};

    assertThatCode(
            () -> executableValidator.validateParameters(controller, getFeelingReplies, parameters))
        .doesNotThrowAnyException();
  }

  @Test
  void inheritedSizeConstraintIsApplied() {
    Object[] parameters = {1L, 1L, null, 0, null, mock(HttpServletRequest.class)};

    Set<ConstraintViolation<PersonalArtworkFeelingController>> violations =
        executableValidator.validateParameters(controller, getFeelingReplies, parameters);

    assertThat(violations).isNotEmpty();
  }
}
