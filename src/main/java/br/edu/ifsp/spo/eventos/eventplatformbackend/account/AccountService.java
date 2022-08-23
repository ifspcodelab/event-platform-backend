package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataUpdateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.MyDataUpdatePasswordDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.password.PasswordResetException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.password.PasswordResetExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtService;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final RecaptchaService recaptchaService;
    private final AuditService auditService;

    private Account getAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new AuthenticationException(AuthenticationExceptionType.NONEXISTENT_ACCOUNT_BY_ID, id.toString())
        );
    }
    
    public Account getUserByAccessToken(String accessToken) {
        DecodedJWT decodedToken = jwtService.decodeToken(accessToken);
        UUID accountId = UUID.fromString(decodedToken.getSubject());

        Account account = getAccount(accountId);

        return account;
    }

    public Account update(String accessToken, MyDataUpdateDto myDataUpdateDto) {
        DecodedJWT decodedToken = jwtService.decodeToken(accessToken);

        if (!recaptchaService.isValid(myDataUpdateDto.getUserRecaptcha())) {
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA, decodedToken.getClaim("email").toString());
        }

        if (accountRepository.existsByCpfAndIdNot(myDataUpdateDto.getCpf(), UUID.fromString(decodedToken.getSubject()))) {
            throw new ResourceAlreadyExistsException(ResourceName.CPF, "CPF", myDataUpdateDto.getCpf());
        }

        UUID accountId = UUID.fromString(decodedToken.getSubject());

        Account account = getAccount(accountId);
        String oldName = account.getName();
        String oldCpf = account.getCpf();

        account.setName(myDataUpdateDto.getName());
        account.setCpf(myDataUpdateDto.getCpf());

        accountRepository.save(account);

        log.info("Account with email={} updated data. Before: name={}, cpf={}. Now: name={}, cpf={}",
                account.getEmail(), oldName, oldCpf, myDataUpdateDto.getName(), myDataUpdateDto.getCpf()
        );

        //TODO: especificar o que foi mudado
        auditService.logUpdate(account, ResourceName.ACCOUNT, "Edição em 'Meus dados'");

        return account;
    }

    public void updatePassword(String accessToken, MyDataUpdatePasswordDto myDataUpdatePasswordDto) {
        DecodedJWT decodedToken = jwtService.decodeToken(accessToken);

        if (!recaptchaService.isValid(myDataUpdatePasswordDto.getUserRecaptcha())) {
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA, decodedToken.getClaim("email").toString());
        }

        if (myDataUpdatePasswordDto.getCurrentPassword().equals(myDataUpdatePasswordDto.getNewPassword())) {
            throw new MyDataResetPasswordException(MyDataResetPasswordExceptionType.SAME_PASSWORD, decodedToken.getClaim("email").toString());
        }

        UUID accountId = UUID.fromString(decodedToken.getSubject());
        Account account = getAccount(accountId);

        if (!passwordEncoder.matches(myDataUpdatePasswordDto.getCurrentPassword(), account.getPassword())) {
            throw new MyDataResetPasswordException(MyDataResetPasswordExceptionType.INCORRECT_PASSWORD, account.getEmail());
        }

        account.setPassword(passwordEncoder.encode(myDataUpdatePasswordDto.getNewPassword()));
        accountRepository.save(account);

        log.info("Password reset at My Data: account with email={} updated their password", account.getEmail());

        auditService.logUpdate(account, ResourceName.ACCOUNT, "Alteração de senha via edição em 'Meus dados'");
    }
}
