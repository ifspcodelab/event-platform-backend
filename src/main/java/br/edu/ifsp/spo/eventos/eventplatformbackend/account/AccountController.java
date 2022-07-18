package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

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
public class AccountController {

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
        TokensDto tokensDto = new TokensDto(
                "accessToken",
                "RefreshToken"
        );
        return new ResponseEntity<>(tokensDto, HttpStatus.OK);
    }
}
