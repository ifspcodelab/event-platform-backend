package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RegistrationScheduler {
    private final RegistrationService registrationService;

    @Scheduled(fixedRateString = "${scheduler.registration.interval}")
    public void execute() {
        registrationService.cancelAllRegistrationInWaitConfirmation();
    }
}
