package br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuditService {
    private final LogRepository logRepository;

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
}
