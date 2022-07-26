package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationException;
import com.auth0.jwt.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Void> handlerLoginException(AuthenticationException ex){
        log.warn(String.format(ex.getAuthenticationExceptionType().getMessage(), ex.getEmail()));
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(AlgorithmMismatchException.class)
    public ResponseEntity<Void> handlerAlgorithmMismatchException(AlgorithmMismatchException ex){
        log.warn("Algorithm Mismatch Exception", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(SignatureVerificationException.class)
    public ResponseEntity<Void> handlerSignatureVerificationException(SignatureVerificationException ex){
        log.warn("Signature Verification Exception", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ProblemDetail> handlerTokenExpiredException(TokenExpiredException ex){
        ProblemDetail problemDetail = new ProblemDetail("Token Expired", List.of());
        log.warn("Token Expired Exception");
        return new ResponseEntity(problemDetail, HttpStatus.UNAUTHORIZED);
    }
}
