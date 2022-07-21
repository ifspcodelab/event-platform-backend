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
public class ExceptionHandlerApp extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<Violation> violations = ex.getFieldErrors().stream()
                .map(field -> new Violation(field.getField(), field.getDefaultMessage()))
                .collect(Collectors.toList());
        HttpServletRequest servletRequest = ((ServletWebRequest)request).getRequest();
        log.warn("Bad request at {} {}", servletRequest.getMethod(), servletRequest.getRequestURI());
        return new ResponseEntity(violations, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<List<Violation>> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
//        List<Violation> violations = ex.getFieldErrors().stream()
//                .map(field -> new Violation(field.getField(), field.getDefaultMessage()))
//                .collect(Collectors.toList());
//        ex.
//        log.warn("bad request ");
//        return new ResponseEntity(violations, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerResourceNotFoundException(ResourceNotFoundException ex) {
        ProblemDetail problemDetail = new ProblemDetail(
                "Resource not found exception",
                List.of(new Violation(ex.getResourceName(), ex.getMessage()))
        );
        return new ResponseEntity(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handlerResourceNotFoundException(ResourceAlreadyExistsException ex) {
        ProblemDetail problemDetail = new ProblemDetail(
                "Resource already exists exception",
                List.of(new Violation(ex.getResourceName(), ex.getMessage()))
        );
        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceReferentialIntegrityException.class)
    public ResponseEntity<ProblemDetail> resourceReferentialIntegrity(ResourceReferentialIntegrityException ex) {
        ProblemDetail problemDetail = new ProblemDetail(
                "Resource referential integrity exception",
                List.of(
                        new Violation(ex.getArea().getName(), "Area resource"),
                        new Violation(ex.getSpace().getName(), "Space resource")
                )
        );
        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }
}
