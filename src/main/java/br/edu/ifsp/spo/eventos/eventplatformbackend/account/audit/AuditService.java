package br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuditService {
    private final LogRepository logRepository;

    public void log(Action action, ResourceName resourceName, Account account, String resourceData) {
        Log log = new Log(account, action, resourceName, resourceData);

        logRepository.save(log);
    }

    public void log(Action action, ResourceName resourceName, Account account) {
        Log log = new Log(account, action, resourceName, "");

        logRepository.save(log);
    }

    public void logCreate(ResourceName resourceName, Account account, String resourceData) {
        log(Action.CREATE, resourceName, account, resourceData);
    }

    public void logCreate(ResourceName resourceName, Account account) {
        log(Action.CREATE, resourceName, account);
    }

    public void logUpdate(ResourceName resourceName, Account account, String resourceData) {
        log(Action.UPDATE, resourceName, account, resourceData);
    }

    public void logUpdate(ResourceName resourceName, Account account) {
        log(Action.UPDATE, resourceName, account);
    }

}
