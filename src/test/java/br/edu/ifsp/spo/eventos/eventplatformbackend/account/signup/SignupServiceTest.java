package br.edu.ifsp.spo.eventos.eventplatformbackend.account.signup;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountConfig;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.Action;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.LogRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.invalidemail.InvalidEmailRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.email.EmailService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SignupServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountConfig accountConfig;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RecaptchaService recaptchaService;
    @Mock
    private EmailService emailService;
    @Mock
    private AuditService auditService;
    @Mock
    private LogRepository logRepository;
    @Mock
    private InvalidEmailRepository invalidEmailRepository;
    @InjectMocks
    private SignupService signupService;

    @Test
    public void signupServiceShouldNotBeNull() {
        assertThat(signupService).isNotNull();
    }

    @Test
    public void create_ThrowsException_WhenRecaptchaIsInvalid() {
        AccountCreateDto accountCreateDto = getSampleAccountCreateDto();
        when(recaptchaService.isValid(anyString())).thenReturn(Boolean.FALSE);

        RecaptchaException exception = (RecaptchaException) catchThrowable(() -> signupService.create(accountCreateDto));

        assertThat(exception).isInstanceOf(RecaptchaException.class);
        assertThat(exception.getEmail()).isEqualTo(accountCreateDto.getEmail());
        assertThat(exception.getRecaptchaExceptionType()).isEqualTo(RecaptchaExceptionType.INVALID_RECAPTCHA);
    }

    @Test
    public void create_ThrowsException_WhenEmailIsInvalid() {
        AccountCreateDto accountCreateDto = getSampleAccountCreateDto();
        when(recaptchaService.isValid(anyString())).thenReturn(Boolean.TRUE);
        when(invalidEmailRepository.existsByEmail(anyString())).thenReturn(Boolean.TRUE);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(() -> signupService.create(accountCreateDto));

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType()).isEqualTo(BusinessRuleType.INVALID_EMAIL);
    }

    @Test
    public void create_ThrowsException_WhenCpfExistsInUnverifiedAccount() {
        AccountCreateDto accountCreateDto = getSampleAccountCreateDto();
        Account account = AccountFactory.sampleAccount();
        when(recaptchaService.isValid(anyString())).thenReturn(Boolean.TRUE);
        when(invalidEmailRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        when(accountRepository.findByCpfAndStatusUnverified(anyString())).thenReturn(Optional.of(account));

        SignupException exception = (SignupException) catchThrowable(() -> signupService.create(accountCreateDto));

        assertThat(exception).isInstanceOf(SignupException.class);
        assertThat(exception.getSignupRuleType()).isEqualTo(SignupRuleType.SIGNUP_ACCOUNT_WITH_EXISTENT_CPF_NOT_VERIFIED);
        assertThat(exception.getCpf()).isEqualTo(accountCreateDto.getCpf());
        assertThat(exception.getEmail()).isEqualTo(accountCreateDto.getEmail());
    }

    @Test
    public void create_ThrowsException_WhenEmailExistsInUnverifiedAccount() {
        AccountCreateDto accountCreateDto = getSampleAccountCreateDto();
        Account account = AccountFactory.sampleAccount();
        when(recaptchaService.isValid(anyString())).thenReturn(Boolean.TRUE);
        when(invalidEmailRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        when(accountRepository.findByCpfAndStatusUnverified(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findByEmailAndStatusUnverified(anyString())).thenReturn(Optional.of(account));

        SignupException exception = (SignupException) catchThrowable(() -> signupService.create(accountCreateDto));

        assertThat(exception).isInstanceOf(SignupException.class);
        assertThat(exception.getSignupRuleType()).isEqualTo(SignupRuleType.SIGNUP_ACCOUNT_WITH_EXISTENT_EMAIL_NOT_VERIFIED);
        assertThat(exception.getEmail()).isEqualTo(accountCreateDto.getEmail());
        assertThat(exception.getCpf()).isEqualTo(accountCreateDto.getCpf());
    }

    @Test
    public void create_ThrowsException_WhenAccountWithGivenEmailAlreadyExists() {
        AccountCreateDto accountCreateDto = getSampleAccountCreateDto();
        when(recaptchaService.isValid(anyString())).thenReturn(Boolean.TRUE);
        when(invalidEmailRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        when(accountRepository.findByCpfAndStatusUnverified(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findByEmailAndStatusUnverified(anyString())).thenReturn(Optional.empty());
        when(accountRepository.existsByEmail(anyString())).thenReturn(Boolean.TRUE);

        ResourceAlreadyExistsException exception =
                (ResourceAlreadyExistsException) catchThrowable(() -> signupService.create(accountCreateDto));

        assertThat(exception).isInstanceOf(ResourceAlreadyExistsException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.ACCOUNT);
        assertThat(exception.getResourceAttribute()).isEqualTo("email");
        assertThat(exception.getResourceAttributeValue()).isEqualTo(accountCreateDto.getEmail());
    }

    @Test
    public void create_ThrowsException_WhenAccountWithGivenCpfAlreadyExists() {
        AccountCreateDto accountCreateDto = getSampleAccountCreateDto();
        when(recaptchaService.isValid(anyString())).thenReturn(Boolean.TRUE);
        when(invalidEmailRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        when(accountRepository.findByCpfAndStatusUnverified(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findByEmailAndStatusUnverified(anyString())).thenReturn(Optional.empty());
        when(accountRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        when(accountRepository.existsByCpf(anyString())).thenReturn(Boolean.TRUE);

        ResourceAlreadyExistsException exception =
                (ResourceAlreadyExistsException) catchThrowable(() -> signupService.create(accountCreateDto));

        assertThat(exception).isInstanceOf(ResourceAlreadyExistsException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.ACCOUNT);
        assertThat(exception.getResourceAttribute()).isEqualTo("cpf");
        assertThat(exception.getResourceAttributeValue()).isEqualTo(accountCreateDto.getCpf());
    }

    @Test
    public void create_ReturnsAccount_WhenSuccessful() {
        AccountCreateDto accountCreateDto = getSampleAccountCreateDtoB();
        Account account = AccountFactory.sampleAccount();
        when(recaptchaService.isValid(anyString())).thenReturn(Boolean.TRUE);
        when(invalidEmailRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        when(accountRepository.findByCpfAndStatusUnverified(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findByEmailAndStatusUnverified(anyString())).thenReturn(Optional.empty());
        when(accountRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        when(accountRepository.existsByCpf(anyString())).thenReturn(Boolean.FALSE);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account createdAccount = signupService.create(accountCreateDto);

        verify(accountRepository, times(1)).save(any(Account.class));
        verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
        //verify(emailService, times(1)).sendVerificationEmail(any(Account.class), any(VerificationToken.class));
        verify(auditService, times(1))
                .log(any(Account.class), any(Action.class), any(ResourceName.class), any(UUID.class));
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getName()).isEqualTo(accountCreateDto.getName());
        assertThat(createdAccount.getEmail()).isEqualTo(accountCreateDto.getEmail());
        assertThat(createdAccount.getCpf()).isEqualTo(accountCreateDto.getCpf());
        assertThat(createdAccount.getAgreed()).isEqualTo(accountCreateDto.getAgreed());
    }

    @Test
    public void verify_ThrowsException_WhenNonexistentToken() {
        UUID verificationToken = UUID.randomUUID();
        when(verificationTokenRepository.findByToken(any(UUID.class))).thenReturn(Optional.empty());

        SignupException exception = (SignupException) catchThrowable(() -> signupService.verify(verificationToken));

        assertThat(exception).isInstanceOf(SignupException.class);
        assertThat(exception.getSignupRuleType()).isEqualTo(SignupRuleType.NONEXISTENT_TOKEN);
    }

    private AccountCreateDto getSampleAccountCreateDto() {
        return new AccountCreateDto(
                "Shinei Nouzen",
                "shineinouzen@email.com",
                "06011909043",
                "PlainPass@01",
                true,
                "3d1a8ed4-88b5-4e40-bb9c-2ccfdfdc014f"
        );
    }

    private AccountCreateDto getSampleAccountCreateDtoB() {
        return new AccountCreateDto(
                "Marcelo Silva",
                "marcelo01@email.com",
                "66709094030",
                "Senha@01",
                true,
                "3d1a8ed4-88b5-4e40-bb9c-2ccfdfdc014f"
        );
    }
}
