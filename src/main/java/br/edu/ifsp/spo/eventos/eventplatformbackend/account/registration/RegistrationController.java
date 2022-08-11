package br.edu.ifsp.spo.eventos.eventplatformbackend.account.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountMapper;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/accounts")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class RegistrationController {
    private final RegistrationService registrationService;
    private final AccountMapper accountMapper;
    private final RecaptchaService recaptchaService;

    @PostMapping("registration")
    public ResponseEntity<AccountDto> create(@Valid @RequestBody AccountCreateDto accountCreateDto, HttpServletRequest request) throws InterruptedException {
        String response = request.getParameter("re-captcha-response");

        recaptchaService.isValid(response);

        Account account = registrationService.create(accountCreateDto);

        AccountDto accountDto = accountMapper.to(account);

        return new ResponseEntity<>(accountDto, HttpStatus.CREATED);
    }

    @PatchMapping("registration/verification/{token}")
    public ResponseEntity<AccountDto> verification(@PathVariable UUID token) {
        Account account = registrationService.verify(token);

        return ResponseEntity.ok(accountMapper.to(account));
    }
}