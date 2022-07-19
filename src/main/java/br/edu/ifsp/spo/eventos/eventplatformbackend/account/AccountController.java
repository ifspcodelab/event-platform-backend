package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<AccountDto> create(@Valid @RequestBody AccountCreateDto accountCreateDto) {
        AccountDto accountDto = new AccountDto(
                UUID.randomUUID(),
                accountCreateDto.getName(),
                accountCreateDto.getEmail(),
                accountCreateDto.getCpf(),
                accountCreateDto.getAgreed()
        );
        return new ResponseEntity<>(accountDto, HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<TokensDto> login(@Valid @RequestBody LoginCreateDto loginCreateDto)
    {
        Account account = accountService.login(loginCreateDto);



        TokensDto tokensDto = new TokensDto(
                jwtService.generateJwt(account),
                "RefreshToken"
        );
        return new ResponseEntity<>(tokensDto, HttpStatus.OK);
    }
}
