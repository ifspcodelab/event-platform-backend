package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<AccountDto> create(@Valid @RequestBody AccountCreateDto accountCreateDto) {
        Account account = accountService.create(accountCreateDto);

        AccountDto accountDto = accountMapper.to(account);

        return new ResponseEntity<>(accountDto, HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<TokensDto> login(@Valid @RequestBody LoginCreateDto loginCreateDto)
    {
        Account account = accountService.login(loginCreateDto);



        TokensDto tokensDto = new TokensDto(
                jwtService.generateAccessToken(account),
                jwtService.generateRefreshToken(account, UUID.randomUUID())
        );
        return new ResponseEntity<>(tokensDto, HttpStatus.OK);
    }
}
