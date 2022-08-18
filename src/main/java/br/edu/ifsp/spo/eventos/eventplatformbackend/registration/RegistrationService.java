package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final EventRepository eventRepository;
    private final SubeventRepository subeventRepository;
    private final ActivityRepository activityRepository;
    private final SessionRepository sessionRepository;
    private final AccountRepository accountRepository;
    private final RegistrationRepository registrationRepository;

    public Registration create(UUID accountId, UUID eventId, UUID activityId, UUID sessionId) {
        var account = getAccount(accountId);
        var session = getSession(sessionId);

        return registrationRepository.save(Registration.createWithConfirmedStatus(account,session));
    }

    public Registration cancel(UUID accountId, UUID eventId, UUID activityId, UUID sessionId, UUID registrationId) {
        return null;
    }

    public Registration create(UUID accountId, UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        return null;
    }

    public Registration cancel(UUID accountId, UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, UUID registrationId) {
        return null;
    }

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACCOUNT, accountId));
    }

    private Activity getActivity(UUID activityId) {
        return activityRepository.findById(activityId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACTIVITY, activityId));
    }

    private Session getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SESSION, sessionId));
    }
}
