package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerApp {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Violation>> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Violation> violations = ex.getFieldErrors().stream()
                .map(field -> new Violation(field.getField(), field.getDefaultMessage()))
                .collect(Collectors.toList());

        log.warn("Bad request at {} {}", request.getMethod(), request.getRequestURI());
        return new ResponseEntity(violations, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = new ProblemDetail(
                "Resource not found exception",
                List.of(new Violation(ex.getResourceName(), ex.getMessage()))
        );

        log.warn("Resource not found at {} {}", request.getMethod(), request.getRequestURI());
        return new ResponseEntity(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handlerResourceAlreadyExistsException(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = new ProblemDetail(
                "Resource already exists exception",
                List.of(new Violation(ex.getResourceName(), ex.getMessage()))
        );

        log.warn("Resource already exists at {} {}", request.getMethod(), request.getRequestURI());
        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceReferentialIntegrityException.class)
    public ResponseEntity<ProblemDetail> resourceReferentialIntegrity(ResourceReferentialIntegrityException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = new ProblemDetail(
                "Resource referential integrity exception",
                List.of(
                        new Violation(ex.getPrimary().getName(), "Primary resource"),
                        new Violation(ex.getRelated().getName(), "Related resource")
                )
        );

        log.warn("Resource referential integrity at {} {}", request.getMethod(), request.getRequestURI());
        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }
}
