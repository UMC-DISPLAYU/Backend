package com.example.demo.domain.personalartworkcommunication.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

import com.example.demo.domain.personalartworkcommunication.application.command.DeletePersonalArtworkQuestionReplyService;
import com.example.demo.domain.personalartworkcommunication.application.command.DeletePersonalArtworkQuestionService;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkQuestionReplyService;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkQuestionService;
import com.example.demo.domain.personalartworkcommunication.application.query.GetPersonalArtworkQuestionsService;
import com.example.demo.domain.personalartworkcommunication.presentation.mapper.PersonalArtworkQuestionPresentationMapper;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonalArtworkQuestionControllerValidationTest {

  private PersonalArtworkQuestionController controller;
  private ExecutableValidator executableValidator;
  private Method getQuestions;

  @BeforeEach
  void setUp() throws NoSuchMethodException {
    controller =
        new PersonalArtworkQuestionController(
            mock(PersonalArtworkQuestionService.class),
            mock(DeletePersonalArtworkQuestionService.class),
            mock(DeletePersonalArtworkQuestionReplyService.class),
            mock(PersonalArtworkQuestionReplyService.class),
            mock(GetPersonalArtworkQuestionsService.class),
            mock(PersonalArtworkQuestionPresentationMapper.class));
    executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
    getQuestions =
        PersonalArtworkQuestionController.class.getMethod(
            "getQuestions",
            Long.class,
            Long.class,
            int.class,
            AuthUser.class,
            HttpServletRequest.class);
  }

  @Test
  void inheritedParameterConstraintsDoNotConflict() {
    Object[] parameters = {1L, null, 10, null, mock(HttpServletRequest.class)};

    assertThatCode(
            () -> executableValidator.validateParameters(controller, getQuestions, parameters))
        .doesNotThrowAnyException();
  }

  @Test
  void inheritedSizeConstraintIsApplied() {
    Object[] parameters = {1L, null, 0, null, mock(HttpServletRequest.class)};

    Set<ConstraintViolation<PersonalArtworkQuestionController>> violations =
        executableValidator.validateParameters(controller, getQuestions, parameters);

    assertThat(violations).isNotEmpty();
  }
}
