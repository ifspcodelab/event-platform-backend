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

    public void log(Account account, Action action, ResourceName resourceName, String resourceData) {
        Log log = new Log(account, action, resourceName, resourceData);

        logRepository.save(log);
    }

    public void log(Account account, Action action, ResourceName resourceName) {
        Log log = new Log(account, action, resourceName, "");

        logRepository.save(log);
    }

    public void logCreate(Account account, ResourceName resourceName, String resourceData) {
        log(account, Action.CREATE, resourceName, resourceData);
    }

    public void logCreate(Account account, ResourceName resourceName) {
        log(account, Action.CREATE, resourceName);
    }

    public void logUpdate(Account account, ResourceName resourceName, String resourceData) {
        log(account, Action.UPDATE, resourceName, resourceData);
    }

    public void logUpdate(Account account, ResourceName resourceName) {
        log(account, Action.UPDATE, resourceName);
    }

    public void logDelete(Account account, ResourceName resourceName, String resourceData) {
        log(account, Action.DELETE, resourceName, resourceData);
    }

    public void logDelete(Account account, ResourceName resourceName) {
        log(account, Action.DELETE, resourceName);
    }
}
