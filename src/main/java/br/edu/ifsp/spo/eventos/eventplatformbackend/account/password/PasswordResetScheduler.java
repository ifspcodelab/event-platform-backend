package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.registration.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PasswordResetScheduler {
    private final PasswordResetService passwordResetService;

    @Scheduled(fixedRateString = "${scheduler.password-reset.interval}")
    public void execute() {
        passwordResetService.removePasswordResetTokens();
    }
}
