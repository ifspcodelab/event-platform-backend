package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Account findById(UUID account) {
        return getAccount(account);
    }


    public Account update(UUID accountId, AccountDto dto) {
        Account account = getAccount(accountId);

        account.setName(dto.getName());
        account.setEmail(dto.getEmail());
        account.setCpf(dto.getCpf());
        account.setAgreed(dto.getAgreed());
        account.setRole(AccountRole.valueOf(dto.getRole()));
        account.setVerified(dto.getVerified());
        log.info("Account with name={} and email={} was updated", account.getName(), account.getEmail());

        return accountRepository.save(account);
    }

    public Account findByName(String name) {
        return getAccount(name);
    }

    public void delete(UUID accountId) {
        Account account = getAccount(accountId);
        accountRepository.deleteById(accountId);
        log.info("Delete account id={}, name={}, email={}", account.getId(), account.getName(), account.getEmail());
    }


    public Page<Account> findAllByName(Pageable pageable, String name) {
        return accountRepository.findAllByName(pageable, name);
    }

    public Page<Account> findAllByEmail(Pageable pageable, String email) {
        return accountRepository.findAllByEmail(pageable, email);
    }

    public Page<Account> findAllByCpf(Pageable pageable, String cpf) {
        return accountRepository.findAllByCpf(pageable, cpf);
    }

    private Account getAccount(UUID userId) {
        return accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACCOUNT, userId));
    }
    private Account getAccount(String accountName) {
        return accountRepository.findByName(accountName)
                .orElseThrow(() -> new UserNotFoundException(ResourceName.ACCOUNT, accountName));
    }



}
