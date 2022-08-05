package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrganizerSubeventService {
    private final OrganizerSubeventRepository organizerSubeventRepository;
    private final AccountRepository accountRepository;
    private final EventRepository eventRepository;
    private final SubeventRepository subeventRepository;

    public OrganizerSubevent create(UUID eventId, UUID subeventId, OrganizerSubeventCreateDto dto) {
        Account account = getAccount(dto.getAccountId());
        Event event = getEvent(eventId);
        Subevent subevent = getSubevent(subeventId);

        if(organizerSubeventRepository.existsByAccountAndEventId(account, eventId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACCOUNT, "account", account.getName());
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_CREATE_WITH_CANCELED_STATUS);
        }

//        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
//            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_SUBEVENT_CREATE_WITH_CANCELED_STATUS);
//        }

        OrganizerSubevent organizerSubevent = new OrganizerSubevent(dto.getOrganizerSubeventType(), account, event, subevent);
        return organizerSubeventRepository.save(organizerSubevent);
    }

    public List<OrganizerSubevent> findAll(UUID eventId, UUID subeventId) {
        checkEventExists(eventId);
        checkSubeventExists(subeventId);
        return organizerSubeventRepository.findAllBySubeventId(subeventId);
    }

    public void delete(UUID eventId, UUID subeventId, UUID organizerSubeventId) {
        OrganizerSubevent organizerSubevent = getOrganizerSubevent(organizerSubeventId);
        checkEventExists(eventId);
        checkSubeventExists(subeventId);
        organizerSubeventRepository.delete(organizerSubevent);
    }

    private OrganizerSubevent getOrganizerSubevent(UUID organizerSubeventId) {
        return organizerSubeventRepository.findById(organizerSubeventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ORGANIZERSUBEVENT, organizerSubeventId));
    }

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACCOUNT, accountId));
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
    }

    private Subevent getSubevent(UUID subeventId) {
        return subeventRepository.findById(subeventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SUBEVENT, subeventId));
    }

    private void checkEventExists(UUID eventId) {
        if(!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(ResourceName.EVENT, eventId);
        }
    }

    private void checkSubeventExists(UUID subeventId) {
        if(!subeventRepository.existsById(subeventId)) {
            throw new ResourceNotFoundException(ResourceName.SUBEVENT, subeventId);
        }
    }
}
