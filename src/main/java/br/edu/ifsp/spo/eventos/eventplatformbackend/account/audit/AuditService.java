package br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication.AuthenticationExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuditService {
    private final LogRepository logRepository;
    private final AccountRepository accountRepository;

    public void log(Account account, Action action, ResourceName resourceName, String resourceData, UUID resourceId) {
        Log log = new Log(account, action, resourceName, resourceData, resourceId);

        logRepository.save(log);
    }

    public void log(Account account, Action action, ResourceName resourceName, UUID resourceId) {
        Log log = new Log(account, action, resourceName, "", resourceId);

        logRepository.save(log);
    }

    public void logCreate(Account account, ResourceName resourceName, String resourceData, UUID resourceId) {
        log(account, Action.CREATE, resourceName, resourceData, resourceId);
    }

    public void logCreate(Account account, ResourceName resourceName, UUID resourceId) {
        log(account, Action.CREATE, resourceName, resourceId);
    }

    public void logUpdate(Account account, ResourceName resourceName, String resourceData, UUID resourceId) {
        log(account, Action.UPDATE, resourceName, resourceData, resourceId);
    }

    public void logUpdate(Account account, ResourceName resourceName, UUID resourceId) {
        log(account, Action.UPDATE, resourceName, resourceId);
    }

    public void logDelete(Account account, ResourceName resourceName, String resourceData, UUID resourceId) {
        log(account, Action.DELETE, resourceName, resourceData, resourceId);
    }

    public void logDelete(Account account, ResourceName resourceName, UUID resourceId) {
        log(account, Action.DELETE, resourceName, resourceId);
    }

    public void logAdmin(Action action, ResourceName resourceName, UUID resourceId) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var adminId = jwtUserDetails.getId();
        log(getAccount(adminId), action, resourceName, resourceId);
    }

    public void logAdminUpdate(ResourceName resourceName, String resourceData, UUID resourceId) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var adminId = jwtUserDetails.getId();
        log(getAccount(adminId), Action.UPDATE, resourceName, resourceData, resourceId);
    }

    public void logAdminDelete(ResourceName resourceName, UUID resourceId) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var adminId = jwtUserDetails.getId();
        log(getAccount(adminId), Action.DELETE, resourceName, resourceId);
    }

    private Account getAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new AuthenticationException(AuthenticationExceptionType.NONEXISTENT_ACCOUNT_BY_ID, id.toString())
        );
    }
}
