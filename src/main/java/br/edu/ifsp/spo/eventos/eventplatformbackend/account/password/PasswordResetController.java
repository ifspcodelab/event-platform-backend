package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/accounts/password")
@AllArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("forgot")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordCreateDto forgotPasswordCreateDto) {
        passwordResetService.createResetPasswordRequest(forgotPasswordCreateDto);

        return ResponseEntity.accepted().build();

    }

    @PostMapping("reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetDto dto){
        passwordResetService.resetPassword(dto);

        return ResponseEntity.noContent().build();
    }

}
