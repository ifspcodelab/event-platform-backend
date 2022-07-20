package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResetPasswordException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountConfig accountConfig;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public Account create(AccountCreateDto dto) {
        if(accountRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("account", "e-mail", dto.getEmail());
        }
        if(accountRepository.existsByCpf(dto.getCpf())) {
            throw new ResourceAlreadyExistsException("account", "cpf", dto.getCpf());
        }

        Account account = new Account(
                dto.getName(),
                dto.getEmail(),
                dto.getCpf(),
                dto.getPassword(),
                dto.getAgreed()
        );

        account.setVerified(true);

        account = accountRepository.save(account);

        VerificationToken verificationToken = new VerificationToken(account,accountConfig.getVerificationTokenExpiresIn());

        verificationTokenRepository.save(verificationToken);

        return account;
    }

    public void createResetPasswordRequest(ForgotPasswordCreateDto forgotPasswordCreateDto) {
        Account account = accountRepository.findByEmail(forgotPasswordCreateDto.getEmail())
                .orElseThrow(()->
                        new ResetPasswordException(
                                String.format("Forgot email not found %s", forgotPasswordCreateDto.getEmail())
                        )
                );

        if (!account.getVerified()){
            throw new ResetPasswordException(
                    String.format("Forgot email account not verified id: %s | email: %s",
                    account.getId(),
                    forgotPasswordCreateDto.getEmail()));
        }

        if (passwordResetTokenRepository.existsByAccountAndExpiresInAfter(account, Instant.now())){
            throw new ResetPasswordException(
                    String.format("Forgot email account already have a open request id: %s | email: %s",
                            account.getId(),
                            forgotPasswordCreateDto.getEmail()));
        }


        PasswordResetToken passwordResetToken =
                new PasswordResetToken(account, accountConfig.getPasswordResetTokenExpiresIn());

        passwordResetTokenRepository.save(passwordResetToken);
    }

    public void resetPassword(PasswordResetDto dto){
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() ->
                        new ResetPasswordException(
                                String.format("Reset password token not found %s", dto.getToken())
                        )
                );

        if (passwordResetTokenRepository.existsByTokenAndExpiresInBefore(passwordResetToken.getToken(), Instant.now())){
            throw new ResetPasswordException(
                    String.format("Reset password token expired id: %s | email: %s",
                            passwordResetToken.getAccount().getId(),
                            passwordResetToken.getAccount().getEmail()));
        }

        Account account = accountRepository.findByEmail(passwordResetToken.getAccount().getEmail())
                .orElseThrow(() ->
                        new ResetPasswordException(
                                String.format("Reset password email account does not exist %s",
                                        passwordResetToken.getAccount().getEmail())
                        )
                );

        account.setPassword(dto.getPassword());
        accountRepository.save(account);

        passwordResetTokenRepository.deleteById(passwordResetToken.getId());
    }
}