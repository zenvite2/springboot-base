package com.ptit.zenvite.exception;

import com.ptit.zenvite.service.LanguageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    LanguageService languageService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleValidationException(MethodArgumentNotValidException ex) {

        List<GlobalException> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> GlobalException.builder()
                        .title(fieldError.getDefaultMessage())
                        .errorKey(fieldError.getField())
                        .entityName(fieldError.getObjectName())
                        .build()
                )
                .collect(Collectors.toList());

        Problem problem = Problem.builder()
                .withTitle("Validation Error")
                .withStatus(Status.BAD_REQUEST)
                .withDetail("MethodArgumentNotValidException")
                .with("errors", validationErrors)
                .build();

        return ResponseEntity
                .status(Status.BAD_REQUEST.getStatusCode())
                .body(problem);
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<Problem> handleGlobalException(GlobalException ex) {
        Problem problem = Problem.builder()
                .withTitle(ex.getTitle())
                .withStatus(ex.getStatus())
                .withDetail(languageService.getMessage(ex.getTitle()))
                .with("errorKey", ex.getErrorKey())
                .with("entityName", ex.getEntityName())
                .build();

        return ResponseEntity
                .status(500)
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Problem> handleDefaultException(Exception ex) {
        Problem problem = Problem.builder()
                .withTitle("Internal Server Error")
                .withStatus(Status.INTERNAL_SERVER_ERROR)
                .withDetail(ex.getMessage())
                .build();

        return ResponseEntity
                .status(Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .body(problem);
    }
}
