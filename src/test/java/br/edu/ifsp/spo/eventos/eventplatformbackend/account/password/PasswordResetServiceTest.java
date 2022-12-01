package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountConfig;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.email.EmailService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

    @Test
    public void createResetPasswordRequest_ThrowsException_WhenRecaptchaIsInvalid() {
        ForgotPasswordCreateDto forgotPasswordCreateDto = sampleForgotPasswordCreateDto();
        when(recaptchaService.isValid(anyString())).thenReturn(Boolean.FALSE);

        RecaptchaException exception = (RecaptchaException) catchThrowable(() -> passwordResetService.createResetPasswordRequest(forgotPasswordCreateDto));

        assertThat(exception).isInstanceOf(RecaptchaException.class);
        assertThat(exception.getRecaptchaExceptionType()).isEqualTo(RecaptchaExceptionType.INVALID_RECAPTCHA);
        assertThat(exception.getEmail()).isEqualTo(forgotPasswordCreateDto.getEmail());
    }

    @Test
    public void createResetPasswordRequest_ThrowsException_WhenAccountWithGivenEmailDoestNotExist() {
        ForgotPasswordCreateDto forgotPasswordCreateDto = sampleForgotPasswordCreateDto();
        when(recaptchaService.isValid(anyString())).thenReturn(Boolean.TRUE);
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        PasswordResetException exception = (PasswordResetException) catchThrowable(() -> passwordResetService.createResetPasswordRequest(forgotPasswordCreateDto));

        assertThat(exception).isInstanceOf(PasswordResetException.class);
        assertThat(exception.getPasswordResetExceptionType()).isEqualTo(PasswordResetExceptionType.NONEXISTENT_ACCOUNT);
        assertThat(exception.getEmail()).isEqualTo(forgotPasswordCreateDto.getEmail());
    }

    @Test
    public void createResetPasswordRequest_ThrowsException_WhenAccountIsUnverified() {
        ForgotPasswordCreateDto forgotPasswordCreateDto = sampleForgotPasswordCreateDtoB();
        Account account = AccountFactory.sampleAccount();
        when(recaptchaService.isValid(anyString())).thenReturn(Boolean.TRUE);
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.of(account));

        PasswordResetException exception = (PasswordResetException) catchThrowable(() -> passwordResetService.createResetPasswordRequest(forgotPasswordCreateDto));

        assertThat(exception).isInstanceOf(PasswordResetException.class);
        assertThat(exception.getPasswordResetExceptionType()).isEqualTo(PasswordResetExceptionType.UNVERIFIED_ACCOUNT);
        assertThat(exception.getEmail()).isEqualTo(forgotPasswordCreateDto.getEmail());
    }

    private ForgotPasswordCreateDto sampleForgotPasswordCreateDto() {
        return new ForgotPasswordCreateDto(
                "shineinouzen@email.com",
                UUID.randomUUID().toString()
        );
    }

    private ForgotPasswordCreateDto sampleForgotPasswordCreateDtoB() {
        return new ForgotPasswordCreateDto(
                "marcelo01@email.com",
                UUID.randomUUID().toString()
        );
    }
}
