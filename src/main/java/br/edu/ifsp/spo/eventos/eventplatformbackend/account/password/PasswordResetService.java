package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountConfig;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public void createResetPasswordRequest(ForgotPasswordCreateDto dto) {
        recaptchaService.verifyRecaptcha(dto.getUserCaptcha());
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
        log.info("Password Reset: token generated for account {}", dto.getEmail());

    }

    @Transactional
    public void resetPassword(PasswordResetDto dto) {

        log.debug("Token received: " + dto.getToken());
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


    public boolean isCaptchaValid(String userCaptcha){

        return true;
    }


}
