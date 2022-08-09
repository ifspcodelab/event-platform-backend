package br.edu.ifsp.spo.eventos.eventplatformbackend.users;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRole;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotExistsAssociationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final AccountRepository accountRepository;

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Account findById(UUID userId) {
        return getUser(userId);
    }


    public Account update(UUID userId, UserUpdateDto dto) {
        Account user = getUser(userId);

        user.setRole(AccountRole.valueOf(dto.getRole()));
        user.setVerified(dto.getVerified());
        log.info("User with name={} and email={} was updated", user.getName(), user.getEmail());

        return accountRepository.save(user);
    }

    public Account findByName(String name) {
        return getUser(name);
    }

    public void delete(UUID userId) {
        Account user = getUser(userId);
        accountRepository.deleteById(userId);
        log.info("Delete user id={}, name={}, email={}", user.getId(), user.getName(), user.getEmail());
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

    private Account getUser(UUID userId) {
        return accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACCOUNT, userId));
    }
    private Account getUser(String userName) {
        return accountRepository.findByName(userName)
                .orElseThrow(() -> new UserNotFoundException(ResourceName.ACCOUNT, userName));
    }



}
