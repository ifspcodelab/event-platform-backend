package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

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

        return accountRepository.save(account);
    }
}
