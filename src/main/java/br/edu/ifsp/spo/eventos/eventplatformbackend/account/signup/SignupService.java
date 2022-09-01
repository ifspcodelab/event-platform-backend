package br.edu.ifsp.spo.eventos.eventplatformbackend.account.signup;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountConfig;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.email.EmailService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.Action;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.LogRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.speaker.Speaker;
import br.edu.ifsp.spo.eventos.eventplatformbackend.speaker.SpeakerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {
    private final AccountRepository accountRepository;
    private final AccountConfig accountConfig;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecaptchaService recaptchaService;
    private final SpeakerRepository speakerRepository;
    private final EmailService emailService;
    private final AuditService auditService;
    private final LogRepository logRepository;

    @Transactional
    public Account create(AccountCreateDto dto) {
        if (!recaptchaService.isValid(dto.getUserRecaptcha())) {
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA, dto.getEmail());
        }

        if(accountRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException(ResourceName.ACCOUNT, "email", dto.getEmail());
        }

        if(accountRepository.existsByCpf(dto.getCpf())) {
            throw new ResourceAlreadyExistsException(ResourceName.ACCOUNT, "cpf", dto.getCpf());
        }

        Account account = new Account(dto.getName(), dto.getEmail(), dto.getCpf(), passwordEncoder.encode(dto.getPassword()), dto.getAgreed());
        try {
            account = accountRepository.save(account);
            log.info("Account with id {} was created", account.getId());

            VerificationToken verificationToken =
                    new VerificationToken(account,accountConfig.getVerificationTokenExpiresIn());
            verificationTokenRepository.save(verificationToken);
            log.debug("Verification token {} for email {} was created", verificationToken.getToken(), account.getEmail());

            Optional<Speaker> optionalSpeaker = speakerRepository.findByCpf(account.getCpf());
            if (optionalSpeaker.isPresent()) {
                Speaker speaker = optionalSpeaker.get();
                speaker.setAccount(account);
                speakerRepository.save(speaker);
                log.info(
                    "Speaker with name={} and email={} was associated with account with id {}",
                    speaker.getName(), speaker.getEmail(), account.getId()
                );
            }

            emailService.sendVerificationEmail(account, verificationToken);
            log.info("Verification email was sent to {}", account.getEmail());
            auditService.log(account, Action.SIGN_UP, ResourceName.ACCOUNT);
            return account;
        } catch (MessagingException ex) {
            log.error("Error when trying to send confirmation e-mail to {}",account.getEmail(), ex);
            throw new BusinessRuleException(BusinessRuleType.MAIL_SERVER_PROBLEM);
        }
    }

    public Account verify(UUID token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new SignupException(SignupRuleType.NONEXISTENT_TOKEN));

        if (verificationToken.getExpiresIn().isBefore(Instant.now())) {
            throw new SignupException(
                SignupRuleType.VERIFICATION_TOKEN_EXPIRED, verificationToken.getAccount().getEmail()
            );
        }

        Account account = verificationToken.getAccount();
        account.setVerified(true);
        accountRepository.save(account);
        log.info("Account with email {} was verified", account.getEmail());
        verificationTokenRepository.delete(verificationToken);
        auditService.logUpdate(account, ResourceName.ACCOUNT, "Conta verificada");
        log.info("Verification token with id {} was deleted", verificationToken.getId());
        return account;
    }

    public List<Account> search(String name, Boolean verified) {
        return accountRepository.findByNameStartingWithIgnoreCaseAndVerified(name.trim(), verified);
    }

    @Transactional
    public void deleteVerificationTokenAndAccount() {
        verificationTokenRepository.findAllByExpiresInBefore(Instant.now()).forEach(token -> {
            Account account = token.getAccount();
            logRepository.deleteAllByAccount(account);
            verificationTokenRepository.delete(token);
            accountRepository.delete(account);
            log.info("Verification token: token {} - removed by signup scheduler", token.getToken());
            log.info("Account: id {}, email {} - removed by signup scheduler", account.getId(), account.getEmail());
        });
    }

    public Account resendEmailRegistration(String resendEmail) {
        Account account = accountRepository.findByEmail(resendEmail)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACCOUNT, resendEmail));

        if (!verificationTokenRepository.existsByAccount(account)) {
            throw new SignupException(SignupRuleType.NONEXISTENT_TOKEN, resendEmail);
        }

        if (!verificationTokenRepository.existsByExpiresInAfter(Instant.now())) {
            throw new SignupException(SignupRuleType.VERIFICATION_TOKEN_EXPIRED, resendEmail);
        }

        if (!account.getRegistrationTimestamp().plusSeconds(60).isBefore(Instant.now())) {
            throw new BusinessRuleException(BusinessRuleType.RESEND_EMAIL_DELAY);
        }

        try {
            VerificationToken verificationToken = verificationTokenRepository.findByAccount(account);
            emailService.sendVerificationEmail(account, verificationToken);
            log.info("Verification email was resent to {}", account.getEmail());
            auditService.log(account, Action.VERIFICATION_TOKEN, ResourceName.ACCOUNT);
            return account;
        } catch (MessagingException ex) {
            log.error("Error when trying to resend confirmation e-mail to {}",account.getEmail(), ex);
            throw new BusinessRuleException(BusinessRuleType.MAIL_SERVER_PROBLEM);
        }
    }
}
