package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountConfig;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.email.EmailService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {
    @Mock
    private AccountConfig accountConfig;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RecaptchaService recaptchaService;
    @Mock
    private EmailService emailService;
    @Mock
    private AuditService auditService;
    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    public void passwordResetService_ShouldNotBeNull() {
        assertThat(passwordResetService).isNotNull();
    }
}
