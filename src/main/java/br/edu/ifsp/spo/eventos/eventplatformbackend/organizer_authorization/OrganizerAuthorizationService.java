package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrganizerAuthorizationService {
    private final OrganizerAuthorizationRepository organizerAuthorizationRepository;

    public List<Event> findAllOrganizerEvents(UUID accountId) {
        return organizerAuthorizationRepository.findAllEventsByOrganizerAccountId(accountId);
    }
}
