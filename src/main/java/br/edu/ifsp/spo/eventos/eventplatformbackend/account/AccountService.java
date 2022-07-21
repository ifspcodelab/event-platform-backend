package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountConfig accountConfig;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

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
                passwordEncoder.encode(dto.getPassword()),
                dto.getAgreed()
        );

        account.setVerified(true);

        account = accountRepository.save(account);

        VerificationToken verificationToken = new VerificationToken(account,accountConfig.getVerificationTokenExpiresIn());

        verificationTokenRepository.save(verificationToken);

        return account;
    }


}
