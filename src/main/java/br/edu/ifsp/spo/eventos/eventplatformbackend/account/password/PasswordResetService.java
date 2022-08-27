package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountConfig;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.EmailService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PasswordResetService {
    private final AccountConfig accountConfig;
    private final AccountRepository accountRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecaptchaService recaptchaService;
    private final EmailService emailService;
    private final AuditService auditService;

    @Transactional
    public void createResetPasswordRequest(ForgotPasswordCreateDto dto) {
        if(!recaptchaService.isValid(dto.getUserRecaptcha())){
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA, dto.getEmail());
        }

        Account account = accountRepository.findByEmail(dto.getEmail())
                .orElseThrow(()->
                        new PasswordResetException(PasswordResetExceptionType.NONEXISTENT_ACCOUNT, dto.getEmail())
                );

        if (!account.getVerified()){
            throw new PasswordResetException(PasswordResetExceptionType.UNVERIFIED_ACCOUNT, dto.getEmail());
        }

        if (passwordResetTokenRepository.existsByAccountAndExpiresInAfter(account, Instant.now())){
            throw new PasswordResetException(PasswordResetExceptionType.OPEN_REQUEST, dto.getEmail());
        }

        try {
            PasswordResetToken passwordResetToken = new PasswordResetToken(account, accountConfig.getPasswordResetTokenExpiresIn());
            passwordResetTokenRepository.save(passwordResetToken);
            emailService.sendPasswordResetEmail(account, passwordResetToken);
            log.info("Password Reset: token generated for account {}", dto.getEmail());
            auditService.logCreate(account, ResourceName.PASSWORD_RESET_TOKEN, "Requisição de alteração de senha em 'Esqueci minha senha'");
            log.info("Password Reset email was sent to {}", account.getEmail());
        } catch (MessagingException ex) {
            log.error("Error when trying to send password reset email to {}",account.getEmail(), ex);
        }
    }

    @Transactional
    public void resetPassword(PasswordResetDto dto) {
        if(!recaptchaService.isValid(dto.getUserRecaptcha())){
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA);
        }

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(UUID.fromString(dto.getToken()))
                .orElseThrow(() ->
                        new PasswordResetException(PasswordResetExceptionType.RESET_TOKEN_NOT_FOUND)
                );

        if(passwordResetToken.isExpired()){
            throw new PasswordResetException(
                    PasswordResetExceptionType.RESET_TOKEN_EXPIRED, passwordResetToken.getAccount().getEmail());
        }

        Account account = passwordResetToken.getAccount();
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        accountRepository.save(account);
        log.info("Password Reset: password update for account: {}", account.getEmail());
        passwordResetTokenRepository.deleteById(passwordResetToken.getId());
        auditService.logUpdate(account, ResourceName.ACCOUNT, "Redefinição de senha via 'Esqueci minha senha'");
        log.info("Password Reset: reset token deleted");
    }

    @Transactional
    public void removePasswordResetTokens() {
        passwordResetTokenRepository.findAllByExpiresInBefore(Instant.now()).forEach(token -> {
            Account account = token.getAccount();
            auditService.logDelete(account, ResourceName.PASSWORD_RESET_TOKEN, "Solicitação de redefinição de senha removida pelo sistema");
            passwordResetTokenRepository.delete(token);
            log.info("Password Reset token: token {}, account id {}, email {} - removed by password reset scheduler", token.getToken(), account.getId(),account.getEmail());
        });
    }

    public void resendEmailForgotPassword(String resendEmail) {
        Account account = accountRepository.findByEmail(resendEmail)
                .orElseThrow(()->
                        new PasswordResetException(PasswordResetExceptionType.NONEXISTENT_ACCOUNT, resendEmail)
                );

        if (!account.getVerified()){
            throw new PasswordResetException(PasswordResetExceptionType.UNVERIFIED_ACCOUNT, resendEmail);
        }

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByAccount(account)
                .orElseThrow(() ->
                        new PasswordResetException(PasswordResetExceptionType.RESET_TOKEN_NOT_FOUND)
                );

        if(passwordResetToken.isExpired()){
            throw new PasswordResetException(
                    PasswordResetExceptionType.RESET_TOKEN_EXPIRED, passwordResetToken.getAccount().getEmail());
        }

        if (!passwordResetToken.getExpiresIn().minusSeconds(accountConfig.getPasswordResetTokenExpiresIn()).plusSeconds(60).isBefore(Instant.now())) {
            throw new BusinessRuleException(BusinessRuleType.RESEND_EMAIL_DELAY);
        }

        try {
            emailService.sendPasswordResetEmail(account, passwordResetToken);
            log.info("Password Reset email was resent to {}", account.getEmail());
        } catch (MessagingException ex) {
            log.error("Error when trying to resend password reset email to {}",account.getEmail(), ex);
        }
    }
}
