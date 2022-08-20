package br.edu.ifsp.spo.eventos.eventplatformbackend.account.signup;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountMapper;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/accounts")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class SignupController {
    private final SignupService signupService;
    private final AccountMapper accountMapper;
    private final RecaptchaService recaptchaService;

    @PostMapping("registration")
    public ResponseEntity<AccountDto> create(@Valid @RequestBody AccountCreateDto accountCreateDto, HttpServletRequest request) throws InterruptedException {
        String response = request.getParameter("re-captcha-response");

        recaptchaService.isValid(response);

        Account account = signupService.create(accountCreateDto);

        AccountDto accountDto = accountMapper.to(account);

        return new ResponseEntity<>(accountDto, HttpStatus.CREATED);
    }

    @PatchMapping("registration/verification/{token}")
    public ResponseEntity<AccountDto> verification(@PathVariable UUID token) {
        Account account = signupService.verify(token);

        return ResponseEntity.ok(accountMapper.to(account));
    }

    @GetMapping("searchName/{name}")
    public ResponseEntity<List<AccountDto>> findByName(@PathVariable String name) {
        List<Account> results = signupService.search(name, true);
        return ResponseEntity.ok(accountMapper.to(results));
    }
}