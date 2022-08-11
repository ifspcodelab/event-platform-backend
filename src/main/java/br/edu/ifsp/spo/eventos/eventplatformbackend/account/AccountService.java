package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataUpdateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtService;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {
    private final JwtService jwtService;
    private final AccountRepository accountRepository;
    
    public MyDataDto getUserByAccessToken(String accessToken) {
        DecodedJWT decodedToken = jwtService.decodeToken(accessToken);
        UUID accountId = UUID.fromString(decodedToken.getSubject());

        Account account = getAccount(accountId);
        
        String name = account.getName();
        String email = account.getEmail();
        String cpf = account.getCpf();

        return new MyDataDto(name, email, cpf);
    }

    public Account update(String accessToken, MyDataUpdateDto myDataUpdateDto) {
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
}
