package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataUpdateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataUpdatePasswordDto;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<AccountDto> show(@RequestHeader("Authorization") String accessToken) {
        Account account = accountService.getUserByAccessToken(accessToken.replace("Bearer ", ""));
        AccountDto accountDto = accountMapper.to(account);

        return ResponseEntity.ok(accountDto);
    }

    @PatchMapping("my-data")
    public ResponseEntity<AccountDto> update(@RequestHeader("Authorization") String accessToken, @Valid @RequestBody MyDataUpdateDto myDataUpdateDto) {
        Account account = accountService.update(accessToken.replace("Bearer ", ""), myDataUpdateDto);

        return ResponseEntity.ok(accountMapper.to(account));
    }

    @PatchMapping("my-data/password")
    public ResponseEntity<Void> updatePassword(@RequestHeader("Authorization") String accessToken, @Valid @RequestBody MyDataUpdatePasswordDto myDataUpdatePasswordDto) {
        accountService.updatePassword(accessToken.replace("Bearer ", ""), myDataUpdatePasswordDto);

        return ResponseEntity.noContent().build();
    }
}
