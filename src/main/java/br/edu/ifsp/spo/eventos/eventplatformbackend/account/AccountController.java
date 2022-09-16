package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.Log;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.LogDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.LogMapper;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountUpdateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("api/v1/accounts")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final LogMapper logMapper;

    @GetMapping()
    public ResponseEntity<Page<AccountDto>> index(
            @RequestParam String searchType,
            @RequestParam String query,
            @PageableDefault(page = 0, size = 20, sort = "name") Pageable pageable) {

        Page<Account> accounts = accountService.getAccounts(pageable, searchType, query);
        return ResponseEntity.ok(accounts.map(accountMapper::to));
    }


    @GetMapping("{accountId}")
        public ResponseEntity<AccountDto> show(@PathVariable UUID accountId) {
        Account account = accountService.findById(accountId);
        AccountDto accountDto = accountMapper.to(account);
        return ResponseEntity.ok(accountDto);
    }


    @PutMapping("{accountId}")
    public ResponseEntity<AccountDto> update(@PathVariable UUID accountId, @RequestBody @Valid AccountUpdateDto dto) {
        Account account = accountService.update(accountId, dto);
        AccountDto accountDto = accountMapper.to(account);
        return ResponseEntity.ok(accountDto);
    }

    @DeleteMapping("{accountId}")
    public ResponseEntity<Void> delete(@PathVariable UUID accountId) {
        accountService.delete(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("my-data")
    public ResponseEntity<AccountDto> show(Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        var accountId = jwtUserDetails.getId();
        Account account = accountService.getUserByAccessToken(accountId);
        AccountDto accountDto = accountMapper.to(account);

        return ResponseEntity.ok(accountDto);
    }

    @PatchMapping("my-data")
    public ResponseEntity<AccountDto> update(@Valid @RequestBody MyDataUpdateDto myDataUpdateDto, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        Account account = accountService.update(jwtUserDetails, myDataUpdateDto);

        return ResponseEntity.ok(accountMapper.to(account));
    }

    @PatchMapping("my-data/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody MyDataUpdatePasswordDto myDataUpdatePasswordDto, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        accountService.updatePassword(jwtUserDetails, myDataUpdatePasswordDto);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("my-data/logs")
    public ResponseEntity<Page<LogDto>> index(
            Authentication authentication,
            @PageableDefault(page = 0, size = 20) Pageable pageable) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        var accountId = jwtUserDetails.getId();
        Page<Log> logs = accountService.findAllLogsByAccountId(accountId, pageable);

        return ResponseEntity.ok(logs.map(logMapper::to));
    }

    @PostMapping("my-data/account-deletion")
    public ResponseEntity<Void> deleteAccountRequest(@RequestHeader("Authorization") String accessToken, @Valid @RequestBody AccountDeletionRequestDto accountDeletionRequestDto) {
        accountService.sendAccountDeletionEmail(accessToken.replace("Bearer ", ""), accountDeletionRequestDto);

        return ResponseEntity.noContent().build();
    }

}
