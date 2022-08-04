package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrganizerService {
    private OrganizerRepository organizerRepository;
    private AccountRepository accountRepository;
    private EventRepository eventRepository;

    public Organizer create(UUID eventId, OrganizerCreateDto organizerDto) {
        Account account = getAccount(organizerDto.getAccountId());
        Event event = getEvent(eventId);

        if(organizerRepository.existsByAccountAndEventId(account, eventId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACCOUNT, "account", account.getName());
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_CREATE_WITH_CANCELED_STATUS);
        }

        Organizer organizer = new Organizer(organizerDto.getType(), account, event);
        return organizerRepository.save(organizer);
    }

    public List<Organizer> findAll(UUID eventId) {
        checkEventExists(eventId);
        return organizerRepository.findAllByEventId(eventId);
    }

    public void delete(UUID eventId, UUID organizerId) {
        Organizer organizer = getOrganizer(organizerId);
        organizerRepository.delete(organizer);
    }

    private Organizer getOrganizer(UUID organizerId) {
        return organizerRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ORGANIZER, organizerId));
    }

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACCOUNT, accountId));
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
    }

    private void checkEventExists(UUID eventId) {
        if(!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(ResourceName.EVENT, eventId);
        }
    }
}
