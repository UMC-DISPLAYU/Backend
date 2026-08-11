package com.example.demo.domain.artworkcommunication.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

import com.example.demo.domain.artworkcommunication.application.command.CreateArtworkQuestionReplyService;
import com.example.demo.domain.artworkcommunication.application.command.CreateArtworkQuestionService;
import com.example.demo.domain.artworkcommunication.application.command.DeleteArtworkQuestionReplyService;
import com.example.demo.domain.artworkcommunication.application.command.DeleteArtworkQuestionService;
import com.example.demo.domain.artworkcommunication.application.query.GetArtworkQuestionsService;
import com.example.demo.domain.artworkcommunication.presentation.mapper.ArtworkQuestionPresentationMapper;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArtworkQuestionControllerValidationTest {

  private ArtworkQuestionController controller;
  private ExecutableValidator executableValidator;
  private Method getQuestions;

  @BeforeEach
  void setUp() throws NoSuchMethodException {
    controller =
        new ArtworkQuestionController(
            mock(CreateArtworkQuestionService.class),
            mock(CreateArtworkQuestionReplyService.class),
            mock(GetArtworkQuestionsService.class),
            mock(DeleteArtworkQuestionService.class),
            mock(DeleteArtworkQuestionReplyService.class),
            mock(ArtworkQuestionPresentationMapper.class));
    executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
    getQuestions =
        ArtworkQuestionController.class.getMethod(
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

    Set<ConstraintViolation<ArtworkQuestionController>> violations =
        executableValidator.validateParameters(controller, getQuestions, parameters);

    assertThat(violations).isNotEmpty();
  }
}
