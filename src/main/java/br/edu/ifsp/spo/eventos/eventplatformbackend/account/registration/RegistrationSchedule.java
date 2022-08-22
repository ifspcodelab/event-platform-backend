package br.edu.ifsp.spo.eventos.eventplatformbackend.account.registration;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class RegistrationSchedule {
    private final RegistrationService registrationService;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void execute() {
        registrationService.deleteVerificationTokensExpired();
        registrationService.deleteAccountsNotVerified();
    }
}
