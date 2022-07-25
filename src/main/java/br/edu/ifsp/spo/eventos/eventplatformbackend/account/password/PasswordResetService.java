package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountConfig;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
@Slf4j
public class PasswordResetService {
    private final AccountConfig accountConfig;
    private final AccountRepository accountRepository;
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;


    public void createResetPasswordRequest(ForgotPasswordCreateDto dto) {
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
    }

    @Transactional
    public void resetPassword(PasswordResetDto dto) {
        PasswordResetToken passwordResetToken = tokenRepo.findByToken(dto.getToken())
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
        tokenRepo.deleteById(passwordResetToken.getId());
    }
}
