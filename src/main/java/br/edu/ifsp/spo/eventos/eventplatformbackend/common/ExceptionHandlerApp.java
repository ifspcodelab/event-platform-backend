package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

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

    @ExceptionHandler(ResetPasswordException.class)
    public ResponseEntity<Void> handlerForgotPasswordEmailNotFound(ResetPasswordException ex){
        log.warn(ex.getReason());
        return ResponseEntity.accepted().build();
    }
}
