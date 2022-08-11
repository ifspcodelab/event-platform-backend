package br.edu.ifsp.spo.eventos.eventplatformbackend.account.user;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/accounts")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final AccountMapper accountMapper;

    @GetMapping("my-data")
    public ResponseEntity<MyDataDto> show(@RequestHeader("Authorization") String accessToken) {
        MyDataDto myDataDto = userService.getUserByAccessToken(accessToken.replace("Bearer ", ""));

        return new ResponseEntity<>(myDataDto, HttpStatus.OK);
    }

    @PatchMapping("my-data")
    public ResponseEntity<AccountDto> update(@RequestHeader("Authorization") String accessToken, @RequestBody MyDataUpdateDto myDataUpdateDto) {
        Account account = userService.update(accessToken.replace("Bearer ", ""), myDataUpdateDto);

        return ResponseEntity.ok(accountMapper.to(account));
    }
}
