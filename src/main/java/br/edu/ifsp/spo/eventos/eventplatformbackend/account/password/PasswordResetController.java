package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/accounts/password")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("forgot")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordCreateDto dto) {
        passwordResetService.createResetPasswordRequest(dto);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetDto dto){
        passwordResetService.resetPassword(dto);
        return ResponseEntity.noContent().build();
    }

}
