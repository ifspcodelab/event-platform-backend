package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlerApp {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Violation>> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<Violation> violations = ex.getFieldErrors().stream()
                .map(field -> new Violation(field.getField(), field.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity(violations, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerResourceNotFoundException(ResourceNotFoundException ex) {
        ProblemDetail problemDetail = new ProblemDetail("Resource not found exception",
                List.of(new Violation(ex.getResourceName(), ex.getMessage())));
        return new ResponseEntity(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handlerResourceNotFoundException(ResourceAlreadyExistsException ex) {
        ProblemDetail problemDetail = new ProblemDetail(
                "Resource already exists exception",
                List.of(new Violation(ex.getResourceName(), ex.getMessage())));
        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceReferentialIntegrityException.class)
    public ResponseEntity<ProblemDetail> resourceReferentialIntegrity(ResourceReferentialIntegrityException ex) {
        ProblemDetail problemDetail = new ProblemDetail(
                "Resource referential integrity exception",
                List.of(new Violation(ex.getLocation().getName(), "Location resource"),
                        new Violation(ex.getArea().getName(), "Area resource"))
        );
        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }
}
