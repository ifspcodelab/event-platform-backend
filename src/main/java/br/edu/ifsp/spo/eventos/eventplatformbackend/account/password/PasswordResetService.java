package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountConfig;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.registration.EmailService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaExceptionType;
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
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final RecaptchaService recaptchaService;
    private final EmailService emailService;


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

        if (tokenRepo.existsByAccountAndExpiresInAfter(account, Instant.now())){
            throw new PasswordResetException(PasswordResetExceptionType.OPEN_REQUEST, dto.getEmail());
        }

        PasswordResetToken passwordResetToken =
                new PasswordResetToken(account, accountConfig.getPasswordResetTokenExpiresIn());
        tokenRepo.save(passwordResetToken);
        log.debug("Password Reset: token generated: {}", passwordResetToken.getToken());
        log.info("Password Reset: token generated for account {}", dto.getEmail());

        try {
            emailService.sendPasswordResetEmail(account, passwordResetToken);
            log.info("Password reset e-mail was sent to {}", account.getEmail());
        } catch (MessagingException ex) {
            log.error("Error when trying to send password reset e-mail to {}",account.getEmail(), ex);
        }
    }

    @Transactional
    public void resetPassword(PasswordResetDto dto) {

        if(!recaptchaService.isValid(dto.getUserRecaptcha())){
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA);
        }

        PasswordResetToken passwordResetToken = tokenRepo.findByToken(UUID.fromString(dto.getToken()))
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
        tokenRepo.deleteById(passwordResetToken.getId());
        log.info("Password Reset: reset token deleted");
    }

}
