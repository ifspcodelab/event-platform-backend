package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.deletion.AccountDeletionException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountDeletionDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountUpdateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataUpdateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataUpdatePasswordDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtService;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    public Account getUserByAccessToken(String accessToken) {
        DecodedJWT decodedToken = jwtService.decodeToken(accessToken);
        UUID accountId = UUID.fromString(decodedToken.getSubject());

        Account account = getAccount(accountId);

        return account;
    }

    public Account update(String accessToken, MyDataUpdateDto myDataUpdateDto) {
        DecodedJWT decodedToken = jwtService.decodeToken(accessToken);

        if (!recaptchaService.isValid(myDataUpdateDto.getUserRecaptcha())) {
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA, decodedToken.getClaim("email").toString());
        }

        if (accountRepository.existsByCpfAndIdNot(myDataUpdateDto.getCpf(), UUID.fromString(decodedToken.getSubject()))) {
            throw new ResourceAlreadyExistsException(ResourceName.CPF, "CPF", myDataUpdateDto.getCpf());
        }

        UUID accountId = UUID.fromString(decodedToken.getSubject());

        Account account = getAccount(accountId);
        String oldName = account.getName();
        String oldCpf = account.getCpf();

        account.setName(myDataUpdateDto.getName());
        account.setCpf(myDataUpdateDto.getCpf());

        accountRepository.save(account);

        log.info("Account with email={} updated data. Before: name={}, cpf={}. Now: name={}, cpf={}",
                account.getEmail(), oldName, oldCpf, myDataUpdateDto.getName(), myDataUpdateDto.getCpf()
        );

        return account;
    }

    public void updatePassword(String accessToken, MyDataUpdatePasswordDto myDataUpdatePasswordDto) {
        DecodedJWT decodedToken = jwtService.decodeToken(accessToken);

        if (!recaptchaService.isValid(myDataUpdatePasswordDto.getUserRecaptcha())) {
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA, decodedToken.getClaim("email").toString());
        }

        if (myDataUpdatePasswordDto.getCurrentPassword().equals(myDataUpdatePasswordDto.getNewPassword())) {
            throw new MyDataResetPasswordException(MyDataResetPasswordExceptionType.SAME_PASSWORD, decodedToken.getClaim("email").toString());
        }

        UUID accountId = UUID.fromString(decodedToken.getSubject());
        Account account = getAccount(accountId);

        if (!passwordEncoder.matches(myDataUpdatePasswordDto.getCurrentPassword(), account.getPassword())) {
            throw new MyDataResetPasswordException(MyDataResetPasswordExceptionType.INCORRECT_PASSWORD, account.getEmail());
        }

        account.setPassword(passwordEncoder.encode(myDataUpdatePasswordDto.getNewPassword()));
        accountRepository.save(account);

        log.info("Password reset at My Data: account with email={} updated their password", account.getEmail());
    }

    public void sendAccountDeletionRequest(String accessToken, AccountDeletionDto accountDeletionDto) {
        DecodedJWT decodedToken = jwtService.decodeToken(accessToken);

        if (!recaptchaService.isValid(accountDeletionDto.getUserRecaptcha())) {
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA, decodedToken.getClaim("email").toString());
        }

        UUID accountId = UUID.fromString(decodedToken.getSubject());
        Account account = getAccount(accountId);

        if (!passwordEncoder.matches(accountDeletionDto.getPassword(), account.getPassword())) {
            throw new AccountDeletionException(account.getEmail());
        }

        log.info("Account deletion request completed for email={}", account.getEmail());
    }
}
