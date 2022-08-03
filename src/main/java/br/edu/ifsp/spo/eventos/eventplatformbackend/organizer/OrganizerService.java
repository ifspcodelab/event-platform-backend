package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
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

        Organizer organizer = new Organizer(organizerDto.getType(), account, event);
        return organizerRepository.save(organizer);
    }

    public List<Organizer> findAll(UUID eventId) {
        return organizerRepository.findAllByEventId(eventId);
    }

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACCOUNT, accountId));
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
    }
}
