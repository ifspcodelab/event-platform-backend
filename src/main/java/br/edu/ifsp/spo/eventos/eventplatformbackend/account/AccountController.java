package br.edu.ifsp.spo.eventos.eventplatformbackend.account;


import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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


    @GetMapping()
    public ResponseEntity<Page<AccountDto>> index(
            @RequestParam String searchType,
            @RequestParam String query,
            @PageableDefault(page = 0, size = 20) Pageable pageable) {

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
    public ResponseEntity<AccountDto> update(@PathVariable UUID accountId, @RequestBody @Valid AccountDto dto) {
        Account account = accountService.update(accountId, dto);
        AccountDto accountDto = accountMapper.to(account);
        return ResponseEntity.ok(accountDto);
    }

    @DeleteMapping("{accountId}")
    public ResponseEntity<Void> delete(@PathVariable UUID accountId) {
        accountService.delete(accountId);
        return ResponseEntity.noContent().build();
    }

}
