package br.edu.ifsp.spo.eventos.eventplatformbackend.account.signup;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountMapper;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/accounts")
@AllArgsConstructor
public class SignupController {
    private final SignupService signupService;
    private final AccountMapper accountMapper;

    @PostMapping("signup")
    public ResponseEntity<AccountDto> create(@Valid @RequestBody AccountCreateDto accountCreateDto) {
        Account account = signupService.create(accountCreateDto);

        AccountDto accountDto = accountMapper.to(account);

        return new ResponseEntity<>(accountDto, HttpStatus.CREATED);
    }

    @PatchMapping("signup/verification/{token}")
    public ResponseEntity<AccountDto> verification(@PathVariable UUID token) {
        Account account = signupService.verify(token);

        return ResponseEntity.ok(accountMapper.to(account));
    }

    @PostMapping("signup/resend-email")
    public ResponseEntity<AccountDto> resendEmail(@RequestBody String resendEmail) {
        Account account = signupService.resendEmailRegistration(resendEmail);
        return  ResponseEntity.ok(accountMapper.to(account));
    }

    @GetMapping("searchName/{name}")
    public ResponseEntity<List<AccountDto>> findByName(@PathVariable String name) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!jwtUserDetails.isAdmin() && !jwtUserDetails.isOrganizer()) {
            throw new BusinessRuleException(BusinessRuleType.UNAUTHORIZED_ACTION);
        }

        List<Account> results = signupService.search(name, AccountStatus.VERIFIED);
        return ResponseEntity.ok(accountMapper.to(results));
    }
}