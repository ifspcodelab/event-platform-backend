package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataUpdateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataUpdatePasswordDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.password.PasswordResetException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.password.PasswordResetExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtService;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    
    public Account getUserByAccessToken(String accessToken) {
        DecodedJWT decodedToken = jwtService.decodeToken(accessToken);
        UUID accountId = UUID.fromString(decodedToken.getSubject());

        Account account = getAccount(accountId);

        return account;
    }

    public Account update(String accessToken, MyDataUpdateDto myDataUpdateDto) {
        if(accountRepository.existsByCpf(myDataUpdateDto.getCpf())) {
            throw new ResourceAlreadyExistsException(ResourceName.CPF, "CPF", myDataUpdateDto.getCpf());
        }

        DecodedJWT decodedToken = jwtService.decodeToken(accessToken);
        UUID accountId = UUID.fromString(decodedToken.getSubject());

        Account account = getAccount(accountId);
        String oldName = account.getName();
        String oldCpf = account.getCpf();

        account.setName(myDataUpdateDto.getName());
        account.setCpf(myDataUpdateDto.getCpf());

        log.info("Account with email={} updated data. Before: name={}, cpf={}. Now: name={}, cpf={}",
                account.getEmail(), oldName, oldCpf, myDataUpdateDto.getName(), myDataUpdateDto.getCpf()
        );

        return accountRepository.save(account);
    }

    private Account getAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new AuthenticationException(AuthenticationExceptionType.NONEXISTENT_ACCOUNT_BY_ID, id.toString())
        );
    }

    public void updatePassword(String accessToken, MyDataUpdatePasswordDto myDataUpdatePasswordDto) {
        //verificar se a currentpassword é igual a password na base de dados;
        DecodedJWT decodedToken = jwtService.decodeToken(accessToken);

        if (!myDataUpdatePasswordDto.getNewPassword().equals(myDataUpdatePasswordDto.getNewPasswordConfirmation())) {
            throw new PasswordResetException(PasswordResetExceptionType.PASSWORD_CONFIRMATION_DOESNT_MATCH, decodedToken.getClaim("email").toString());
        }

        UUID accountId = UUID.fromString(decodedToken.getSubject());

        Account account = getAccount(accountId);

        account.setPassword(passwordEncoder.encode(myDataUpdatePasswordDto.getNewPassword()));
        accountRepository.save(account);

        log.info("Password reset at My Data: account with email={} updated their password", account.getEmail());
    }
}
