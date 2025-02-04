package com.ptit.zenvite.exception;

import com.ptit.zenvite.service.LanguageService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.apache.tomcat.util.http.HeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.violations.ConstraintViolationProblem;

import java.net.URI;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler implements ProblemHandling {

    @Autowired
    LanguageService languageService;

    @Override
    public ResponseEntity<Problem> process(@Nullable ResponseEntity<Problem> entity, NativeWebRequest request) {
        if(entity == null) {
            return null;
        }
        Problem problem = entity.getBody();
        if(!(problem instanceof DefaultProblem)) {
            return entity;
        }
        ProblemBuilder problemBuilder = Problem.builder()
                .withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? Problem.DEFAULT_TYPE : problem.getType())
                .withStatus(problem.getStatus())
                .withTitle(problem.getTitle())
                .with("path", request.getNativeRequest(HttpServletRequest.class).getRequestURI());

        if(problem instanceof ConstraintViolationProblem) {
            problemBuilder.with("violations", ((ConstraintViolationProblem) problem).getViolations())
                    .with("message", "Error validation");
        } else {
            problemBuilder.withCause(((DefaultProblem) problem).getCause())
                    .withDetail(problem.getDetail())
                    .withInstance(problem.getInstance());
            problem.getParameters().forEach(problemBuilder::with);
            if(!problem.getParameters().containsKey("message") && problem.getStatus() != null) {
                problemBuilder.with("message", "error.http." + problem.getStatus().getStatusCode());
            }
        }
        return new ResponseEntity<>(problemBuilder.build(), entity.getHeaders(), entity.getStatusCode());
    }

    @Override
    public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @Nonnull NativeWebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = new java.util.ArrayList<>(bindingResult
                .getFieldErrors()
                .stream()
                .map(f -> new FieldError(f.getObjectName().replaceFirst("DTO$", ""), f.getField(), f.getDefaultMessage().replaceAll(" ", "_")))
                .toList());
        List<FieldError> globalErrors = bindingResult
                .getGlobalErrors()
                .stream()
                .filter(objectError -> objectError.getArguments().length >= 3)
                .map(objectError -> new FieldError(objectError.getObjectName().replaceFirst("DTO$", ""), objectError.getArguments()[2].toString(), objectError.getDefaultMessage()))
                .toList();
        fieldErrors.addAll(globalErrors);
        Problem problem = Problem.builder()
                .withType(URI.create("CONSTRAINT_VIOLENCE"))
                .withTitle("Method argument not valid")
                .withStatus(Status.BAD_REQUEST)
                .with("message", "Error validation")
                .with("errorFields", fieldErrors)
                .build();
        return create(ex, problem, request);
    }

//    @ExceptionHandler
//    public ResponseEntity<Problem> handleBadRequestException(BadRequestException ex, NativeWebRequest request) {
//        return create(
//                ex,
//                request,
//
//        )
//    }

    @ExceptionHandler(value = DataConstraintException.class)
    public ResponseEntity<DataConstraintException> handleDataConstraintException(DataConstraintException ex) {
        return ResponseEntity.badRequest().body(ex);
    }
}
