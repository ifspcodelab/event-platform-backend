package br.edu.ifsp.spo.eventos.eventplatformbackend.account.signup;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SignupScheduler {
    private final SignupService signupService;

    @Scheduled(fixedRateString = "${scheduler.signup.interval}")
    public void execute() {
        signupService.deleteVerificationTokenAndAccount();
    }
}
