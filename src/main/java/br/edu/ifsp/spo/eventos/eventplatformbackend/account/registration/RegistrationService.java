package br.edu.ifsp.spo.eventos.eventplatformbackend.account.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountConfig;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {
    private final AccountRepository accountRepository;
    private final AccountConfig accountConfig;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecaptchaService recaptchaService;

    @Transactional
    public Account create(AccountCreateDto dto) {
        if (!recaptchaService.isValid(dto.getUserRecaptcha())) {
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA, dto.getEmail());
        }

        if(accountRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException(ResourceName.ACCOUNT, "e-mail", dto.getEmail());
        }
        if(accountRepository.existsByCpf(dto.getCpf())) {
            throw new ResourceAlreadyExistsException(ResourceName.ACCOUNT, "cpf", dto.getCpf());
        }

        Account account = new Account(
                dto.getName().strip(),
                dto.getEmail(),
                dto.getCpf(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getAgreed()
        );

        account = accountRepository.save(account);

        log.info("Account with id {} was created", account.getId());

        VerificationToken verificationToken =
                new VerificationToken(account,accountConfig.getVerificationTokenExpiresIn());

        verificationTokenRepository.save(verificationToken);

        log.debug("Verification token {} for email {} was created", verificationToken.getToken(), account.getEmail());

        return account;
    }

    public Account verify(UUID token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RegistrationException(RegistrationRuleType.NONEXISTENT_TOKEN));

        if (verificationToken.getExpiresIn().isBefore(Instant.now())) {
            throw new RegistrationException(
                    RegistrationRuleType.VERIFICATION_TOKEN_EXPIRED, verificationToken.getAccount().getEmail()
            );
        }

        Account account = verificationToken.getAccount();
        account.setVerified(true);
        accountRepository.save(account);
        log.info("Account with e-mail {} was verified", account.getEmail());
        verificationTokenRepository.delete(verificationToken);
        log.info("Verification token with id {} was deleted", verificationToken.getId());
        return account;
    }
}
