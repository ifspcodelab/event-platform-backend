package br.edu.ifsp.spo.eventos.eventplatformbackend.account.user;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtService;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
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

    private Account getAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new AuthenticationException(AuthenticationExceptionType.NONEXISTENT_ACCOUNT_BY_ID, id.toString())
        );
    }
}
