package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.password.PasswordResetException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static br.edu.ifsp.spo.eventos.eventplatformbackend.account.password.PasswordResetExceptionType.RESET_TOKEN_NOT_FOUND;

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
        String message = "Resource not found with id " + ex.getResourceId();
        ProblemDetail problemDetail = new ProblemDetail(
                "Resource not found exception",
                List.of(new Violation(ex.getResourceName().getName(), message))
        );

        log.warn("Resource not found at {} {}", request.getMethod(), request.getRequestURI());
        return new ResponseEntity(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handlerResourceAlreadyExistsException(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        String message = String.format(
                "Resource %s already exists with %s %s",
                ex.getResourceName().getName(),
                ex.getResourceAttribute(),
                ex.getResourceAttributeValue()
        );
        ProblemDetail problemDetail = new ProblemDetail(
                "Resource already exists exception",
                List.of(new Violation(ex.getResourceName().getName(), message))
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

    @ExceptionHandler(ResourceNotExistsAssociationException.class)
    public ResponseEntity<ProblemDetail> resourceNotExistsAssociationException(ResourceNotExistsAssociationException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = new ProblemDetail(
                "Resource not exists association exception",
                List.of(
                        new Violation(ex.getPrimary().getName(), "Primary resource"),
                        new Violation(ex.getRelated().getName(), "Related resource")
                )
        );

        log.warn("Resource not exists association exception at {} {}", request.getMethod(), request.getRequestURI());
        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ProblemDetail> handlerBusinessRuleException(BusinessRuleException ex) {
        ProblemDetail problemDetail = new ProblemDetail(
                "Business rule exception",
                List.of(new Violation(ex.getBusinessRuleType().name(), ex.getBusinessRuleType().getMessage()))
        );

        log.warn(
                "Business rule exception: name={}, message={}",
                ex.getBusinessRuleType().name(),
                ex.getBusinessRuleType().getMessage()
        );

        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<Void> handlerForgotPasswordEmailNotFound(PasswordResetException ex){
        log.warn(String.format(ex.getPasswordResetExceptionType().getMessage(), ex.getEmail()));
        if(ex.getPasswordResetExceptionType().equals(RESET_TOKEN_NOT_FOUND)){
            ProblemDetail problemDetail = new ProblemDetail("Token not valid", List.of());
            return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
        }
        return ResponseEntity.accepted().build();
    }

    @ExceptionHandler(RecaptchaException.class)
    public ResponseEntity<ProblemDetail> handlerInvalidRecaptcha(RecaptchaException ex){
        log.warn(String.format(ex.getRecaptchaExceptionType().getMessage(), ex.getEmail()));
        ProblemDetail problemDetail = new ProblemDetail("Invalid recaptcha", List.of());

        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }
}
