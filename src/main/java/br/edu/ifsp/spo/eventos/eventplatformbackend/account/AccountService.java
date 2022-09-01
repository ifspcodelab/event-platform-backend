package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountUpdateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataUpdateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataUpdatePasswordDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.apache.commons.lang3.builder.DiffResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RecaptchaService recaptchaService;
    private final AuditService auditService;

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Account findById(UUID account) {
        return getAccount(account);
    }

    public void delete(UUID accountId) {
        Account account = getAccount(accountId);
        accountRepository.deleteById(accountId);
        log.info("Delete account id={}, name={}, email={}", account.getId(), account.getName(), account.getEmail());
    }

    public Page<Account> getAccounts(Pageable pageable, String searchType, String query){

        if(searchType.equals("name")){
            return accountRepository.findUsersWithPartOfName(pageable, query);
        }
        if(searchType.equals("email")){
            return accountRepository.findUsersWithPartOfEmail(pageable, query);
        }
        if(searchType.equals("cpf")){
            return accountRepository.findUsersWithPartOfCpf(pageable, query);
        }
        return accountRepository.findAll(pageable);
    }

    public Account update(UUID accountId, AccountUpdateDto dto) {
        Account account = getAccount(accountId);

        account.setName(dto.getName());
        account.setEmail(dto.getEmail());
        account.setCpf(dto.getCpf());
        account.setRole(AccountRole.valueOf(dto.getRole()));
        account.setVerified(dto.getVerified());
        log.info("Account with name={} and email={} was updated", account.getName(), account.getEmail());

        return accountRepository.save(account);
    }


    private Account getAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new AuthenticationException(AuthenticationExceptionType.NONEXISTENT_ACCOUNT_BY_ID, id.toString())
        );
    }

    public Account getUserByAccessToken(UUID accountId) {
        return getAccount(accountId);
    }

    public Account update(JwtUserDetails jwtUserDetails, MyDataUpdateDto myDataUpdateDto) {
        var accountId = jwtUserDetails.getId();
        var accountEmail = jwtUserDetails.getUsername();

        if (!recaptchaService.isValid(myDataUpdateDto.getUserRecaptcha())) {
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA, accountEmail);
        }

        if (accountRepository.existsByCpfAndIdNot(myDataUpdateDto.getCpf(), accountId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACCOUNT, "CPF", myDataUpdateDto.getCpf());
        }

        Account account = getAccount(accountId);

        Account currentAccount = new Account();
        currentAccount.setName(account.getName());
        currentAccount.setCpf(account.getCpf());
        currentAccount.setAllowEmail(account.getAllowEmail());

        account.setName(myDataUpdateDto.getName());
        account.setCpf(myDataUpdateDto.getCpf());
        account.setAllowEmail(myDataUpdateDto.getAllowEmail());

        DiffResult<?> diffResult = currentAccount.diff(account);

        accountRepository.save(account);

        log.info("Account with email={} updated data. {}", account.getEmail(), diffResult.getDiffs().toString());

        auditService.logUpdate(account, ResourceName.ACCOUNT, String.format("Edição em 'Meus dados': %s", diffResult.getDiffs().toString()), accountId);

        return account;
    }

    public void updatePassword(JwtUserDetails jwtUserDetails, MyDataUpdatePasswordDto myDataUpdatePasswordDto) {
        var accountId = jwtUserDetails.getId();
        var accountEmail = jwtUserDetails.getUsername();

        if (!recaptchaService.isValid(myDataUpdatePasswordDto.getUserRecaptcha())) {
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA, accountEmail);
        }

        if (myDataUpdatePasswordDto.getCurrentPassword().equals(myDataUpdatePasswordDto.getNewPassword())) {
            throw new MyDataResetPasswordException(MyDataResetPasswordExceptionType.SAME_PASSWORD, accountEmail);
        }

        Account account = getAccount(accountId);

        if (!passwordEncoder.matches(myDataUpdatePasswordDto.getCurrentPassword(), account.getPassword())) {
            throw new MyDataResetPasswordException(MyDataResetPasswordExceptionType.INCORRECT_PASSWORD, account.getEmail());
        }

        account.setPassword(passwordEncoder.encode(myDataUpdatePasswordDto.getNewPassword()));
        accountRepository.save(account);

        log.info("Password reset at My Data: account with email={} updated their password", account.getEmail());

        auditService.logUpdate(account, ResourceName.ACCOUNT, "Alteração de senha via edição em 'Meus dados'", accountId);
    }
}
