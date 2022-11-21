package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer.OrganizerRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent.OrganizerSubeventRepository;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private OrganizerRepository organizerRepository;
    @Mock
    private OrganizerSubeventRepository organizerSubeventRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RecaptchaService recaptchaService;
    @Mock
    private AuditService auditService;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void authenticationServiceShouldNotBeNull() {
        assertThat(authenticationService).isNotNull();
    }

    @Test
    public void recaptchaIsNotValid() {
        when(recaptchaService.isValid(any(String.class))).thenReturn(Boolean.FALSE);

        var loginCreateDto = new LoginCreateDto("user@email.com", "123456", "rechaptch_value");

        assertThatThrownBy(() -> authenticationService.login(loginCreateDto))
                .isInstanceOf(RecaptchaException.class);

    }

    @Test
    public void loginWithAccountUNVERIFIED() {
        when(recaptchaService.isValid(any(String.class))).thenReturn(Boolean.TRUE);

        //FIX: Montar o objeto completo
        Account account = new Account();
        account.setStatus(AccountStatus.UNVERIFIED);
        when(accountRepository.findByEmail(any(String.class))).thenReturn(Optional.of(account));

        var loginCreateDto = new LoginCreateDto("user@email.com", "123456", "rechaptch_value");

        assertThatThrownBy(() -> authenticationService.login(loginCreateDto))
                .isInstanceOf(AuthenticationException.class)
                .extracting("authenticationExceptionType", InstanceOfAssertFactories.type(AuthenticationExceptionType.class))
                .isEqualTo(AuthenticationExceptionType.UNVERIFIED_ACCOUNT);
    }
}
