package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;


import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountDto;
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

    @PostMapping("forgot/resend-email")
    public ResponseEntity<Void> resendEmail(@RequestBody String resendEmail) {
        passwordResetService.resendEmailForgotPassword(resendEmail);
        return  ResponseEntity.ok().build();
    }

    @PostMapping("reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetDto dto){
        passwordResetService.resetPassword(dto);
        return ResponseEntity.noContent().build();
    }
}
