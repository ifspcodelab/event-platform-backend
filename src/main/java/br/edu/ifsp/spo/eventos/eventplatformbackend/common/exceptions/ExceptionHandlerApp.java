package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.MyDataResetPasswordException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.MyDataResetPasswordExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.deletion.AccountDeletionException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.deletion.AccountDeletionExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.password.PasswordResetException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.registration.RegistrationException;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static br.edu.ifsp.spo.eventos.eventplatformbackend.account.password.PasswordResetExceptionType.RESET_TOKEN_NOT_FOUND;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerApp {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Violation>> handlerMethodArgumentNotValidException(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        List<Violation> violations = ex.getFieldErrors().stream()
            .map(field -> new Violation(field.getField(), field.getDefaultMessage()))
            .collect(Collectors.toList());

        log.warn("Bad request at {} {}", request.getMethod(), request.getRequestURI());
        return new ResponseEntity(violations, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerResourceNotFoundException(
        ResourceNotFoundException ex,
        HttpServletRequest request
    ) {
        String message = "Recurso não encontrado com valor " + ex.getResourceId();
        ProblemDetail problemDetail = new ProblemDetail(
            "Resource not found exception",
            List.of(new Violation(ex.getResourceName().getName(), message))
        );

        log.warn("Resource not found at {} {}", request.getMethod(), request.getRequestURI());
        return new ResponseEntity(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerUserNotFoundException(
            UserNotFoundException ex,
            HttpServletRequest request
    ) {
        String message = "Usuário não encontrado relacionado à" + ex.getQuery();
        ProblemDetail problemDetail = new ProblemDetail(
                "User not found exception",
                List.of(new Violation(ex.getResourceName().getName(), message))
        );

        log.warn("Resource not found at {} {}", request.getMethod(), request.getRequestURI());
        return new ResponseEntity(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handlerResourceAlreadyExistsException(
        ResourceAlreadyExistsException ex,
        HttpServletRequest request
    ) {
        String message = String.format(
            "Recurso %s já existe com %s %s",
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
    public ResponseEntity<ProblemDetail> resourceReferentialIntegrity(
        ResourceReferentialIntegrityException ex,
        HttpServletRequest request
    ) {
        ProblemDetail problemDetail = new ProblemDetail(
            "Resource referential integrity exception",
            List.of(
                new Violation(ex.getPrimary().getName(), "Recurso principal"),
                new Violation(ex.getRelated().getName(), "Recurso relacionado")
            )
        );

        log.warn("Resource referential integrity at {} {}", request.getMethod(), request.getRequestURI());
        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotExistsAssociationException.class)
    public ResponseEntity<ProblemDetail> resourceNotExistsAssociationException(
        ResourceNotExistsAssociationException ex,
        HttpServletRequest request
    ) {
        ProblemDetail problemDetail = new ProblemDetail(
            String.format("Resource not exists association exception or %s not exists", ex.getRelated().getName()),
            List.of(
                new Violation(ex.getPrimary().getName(), "Recurso principal"),
                new Violation(ex.getRelated().getName(), "Recurso relacionado")
            )
        );

        log.warn(
            "Resource not exists association exception at {} {} or {} not exists",
            request.getMethod(),
            request.getRequestURI(),
            ex.getPrimary().getName()
        );
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
        if (ex.getPasswordResetExceptionType().equals(RESET_TOKEN_NOT_FOUND)){
            ProblemDetail problemDetail = new ProblemDetail("Token not valid", List.of());
            return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
        }
        return ResponseEntity.accepted().build();
    }
    
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ProblemDetail> handlerRegistrationException(RegistrationException ex) {
        String message = String.format(ex.getRegistrationRuleType().getMessage(), ex.getEmail());
        ProblemDetail problemDetail = new ProblemDetail(ex.getRegistrationRuleType().name(), List.of());

        log.warn(message);

        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Void> handlerLoginException(AuthenticationException ex){
        ProblemDetail problemDetail = new ProblemDetail("", List.of());
        if (ex.getAuthenticationExceptionType().equals(AuthenticationExceptionType.UNVERIFIED_ACCOUNT)) {
            problemDetail = new ProblemDetail(
                    String.format("The account for this email is not yet verified"),
                    List.of()
            );
        }
        if (ex.getAuthenticationExceptionType().equals(AuthenticationExceptionType.INCORRECT_PASSWORD)) {
            problemDetail = new ProblemDetail(
                    String.format("Incorrect email or password", ex.getEmail()),
                    List.of()
            );
        }
        log.warn(String.format(ex.getAuthenticationExceptionType().getMessage(), ex.getEmail()));
        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
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

    @ExceptionHandler(RecaptchaException.class)
    public ResponseEntity<ProblemDetail> handlerInvalidRecaptcha(RecaptchaException ex){
        log.warn(String.format(ex.getRecaptchaExceptionType().getMessage(), ex.getEmail()));
        ProblemDetail problemDetail = new ProblemDetail("Invalid recaptcha", List.of());

        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ProblemDetail> handlerMessagingException(MessagingException ex) {
        log.warn("Verification e-mail not sent", ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(MyDataResetPasswordException.class)
    public ResponseEntity<Void> handlerMyDataPasswordResetExceptions(MyDataResetPasswordException ex) {
        ProblemDetail problemDetail = new ProblemDetail("", List.of());

        if (ex.getMyDataResetPasswordExceptionType().equals(MyDataResetPasswordExceptionType.SAME_PASSWORD)) {
            problemDetail = new ProblemDetail(
                    "New password is the same as current password",
                    List.of()
            );
        }
        if (ex.getMyDataResetPasswordExceptionType().equals(MyDataResetPasswordExceptionType.INCORRECT_PASSWORD)) {
            problemDetail = new ProblemDetail(
                    "Current password is incorrect",
                    List.of()
            );
        }
        log.warn(String.format(ex.getMyDataResetPasswordExceptionType().getMessage(), ex.getEmail()));
        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountDeletionException.class)
    public ResponseEntity<ProblemDetail> handlerAccountDeletion(AccountDeletionException ex) {
        ProblemDetail problemDetail = new ProblemDetail(
                "Password invalid",
                List.of());
        log.warn("Password incorrect for account deletion attempt for email={}: ", ex.getEmail());

        return new ResponseEntity(problemDetail, HttpStatus.CONFLICT);
    }
}
