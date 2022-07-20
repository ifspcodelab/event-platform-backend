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
        //
        // Trazer token para a memoria ou lançar exceção
        // Verifica validade do token
        // Altera valor da senha
        // Salva o account no banco
        // apagar o token

    }

}