package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.Action;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization.OrganizerAuthorizationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization.OrganizerAuthorizationExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class OrganizerService {
    private OrganizerRepository organizerRepository;
    private AccountRepository accountRepository;
    private EventRepository eventRepository;
    private final AuditService auditService;
    private final SessionRepository sessionRepository;

    public Organizer create(UUID eventId, OrganizerCreateDto organizerDto) {
        Account account = getAccount(organizerDto.getAccountId());
        Event event = getEvent(eventId);

        if(organizerRepository.existsByAccountAndEventId(account, eventId)) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_CREATE_ALREADY_ASSOCIATED);
        }

        if(!account.getVerified()) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_CREATE_WITH_ACCOUNT_UNVERIFIED);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_CREATE_WITH_CANCELED_STATUS);
        }

        Organizer organizer = new Organizer(organizerDto.getType(), account, event);
        organizerRepository.save(organizer);

        auditService.logAdmin(Action.CREATE, ResourceName.ORGANIZER, organizer.getId());
        auditService.logAdminUpdate(ResourceName.EVENT, String.format("Account of email %s associated to the event's organization", account.getEmail()), eventId);
        auditService.logAdminUpdate(ResourceName.ACCOUNT, String.format("Conta associada à organização do evento %s", event.getTitle()), account.getId());

        return organizer;
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

        auditService.logAdminDelete(ResourceName.ORGANIZER, organizerId);
        auditService.logAdminUpdate(ResourceName.EVENT, String.format("Account of email %s removed from the event's organization", organizer.getAccount().getEmail()), eventId);
        auditService.logAdminUpdate(ResourceName.ACCOUNT, String.format("Conta removida da organização do evento %s", organizer.getEvent().getTitle()), organizer.getAccount().getId());
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

    public List<Event> findAllEvents(UUID accountId) {
        return organizerRepository.findAllEventsByAccountId(accountId);
    }

    public List<Session> findAllSessions(UUID eventId, UUID accountId) {
        checksOrganizerAccess(eventId);

        return organizerRepository.findAllSessionsByAccountIdAndEventId(accountId, eventId);
    }

    public Session findSessionById(UUID eventId, UUID sessionId) {
        checksOrganizerAccess(eventId);
        Session session = getSession(sessionId);
        checksIfEventIsAssociatedToSession(eventId, session);

        return session;
    }

    private Session getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SESSION, sessionId));
    }

    private void checksIfEventIsAssociatedToSession(UUID eventId, Session session) {
        if(!session.getActivity().getEvent().getId().equals(eventId)) {
            throw new ResourceNotExistsAssociationException(ResourceName.SESSION, ResourceName.EVENT);
        }
    }

    private void checksOrganizerAccess(UUID eventId) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var organizerEvents = Stream.concat(
                jwtUserDetails.getCoordinatorEvent().stream(),
                jwtUserDetails.getCollaboratorEvent().stream()
        );
        var existsEvent = organizerEvents.anyMatch(e -> e.equals(eventId.toString()));

        if (!existsEvent) {
            throw new OrganizerAuthorizationException(
                    OrganizerAuthorizationExceptionType.UNAUTHORIZED_EVENT,
                    jwtUserDetails.getUsername(),
                    eventId
            );
        }
    }
}
