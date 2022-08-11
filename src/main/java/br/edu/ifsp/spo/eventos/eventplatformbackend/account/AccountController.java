package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataUpdateDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @GetMapping("my-data")
    public ResponseEntity<MyDataDto> show(@RequestHeader("Authorization") String accessToken) {
        MyDataDto myDataDto = accountService.getUserByAccessToken(accessToken.replace("Bearer ", ""));
        //TODO: usar o AccountDto em vez do MyDataDto;

        return new ResponseEntity<>(myDataDto, HttpStatus.OK);
    }

    @PatchMapping("my-data")
    public ResponseEntity<AccountDto> update(@RequestHeader("Authorization") String accessToken, @Valid @RequestBody MyDataUpdateDto myDataUpdateDto) {
        Account account = accountService.update(accessToken.replace("Bearer ", ""), myDataUpdateDto);

        return ResponseEntity.ok(accountMapper.to(account));
    }
}
