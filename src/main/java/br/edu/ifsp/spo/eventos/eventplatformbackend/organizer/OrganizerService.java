package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class OrganizerService {
    private OrganizerRepository organizerRepository;
    private AccountRepository accountRepository;
    private EventRepository eventRepository;

    public Organizer create(UUID eventId, OrganizerCreateDto organizerDto) {
        Account account = getAccount(organizerDto.getAccountId());
        Event event = getEvent(eventId);

        if(organizerRepository.existsByAccountAndEventId(account, eventId)) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_CREATE_ALREADY_ASSOCIATED);
        }

        if(account.getStatus().equals(AccountStatus.UNVERIFIED)) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_CREATE_WITH_ACCOUNT_UNVERIFIED);
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
        checkEventExists(eventId);

        if(organizer.getEvent().getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_DELETE_WITH_CANCELED_STATUS);
        }

        organizerRepository.delete(organizer);
        log.info("Organizer event deleted: organizer id={}, event id={}", organizerId, eventId);
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
