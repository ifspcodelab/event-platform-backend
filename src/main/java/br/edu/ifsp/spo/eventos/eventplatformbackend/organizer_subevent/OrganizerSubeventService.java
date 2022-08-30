package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.Action;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class OrganizerSubeventService {
    private final OrganizerSubeventRepository organizerSubeventRepository;
    private final AccountRepository accountRepository;
    private final EventRepository eventRepository;
    private final SubeventRepository subeventRepository;
    private final AuditService auditService;

    public OrganizerSubevent create(UUID eventId, UUID subeventId, OrganizerSubeventCreateDto dto) {
        Account account = getAccount(dto.getAccountId());
        Event event = getEvent(eventId);
        Subevent subevent = getSubevent(subeventId);

        if(organizerSubeventRepository.existsByAccountAndSubeventId(account, subeventId)) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_SUBEVENT_CREATE_ALREADY_ASSOCIATED);
        }

        if(!account.getVerified()) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_SUBEVENT_CREATE_WITH_ACCOUNT_UNVERIFIED);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_CREATE_WITH_CANCELED_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_SUBEVENT_CREATE_WITH_CANCELED_STATUS);
        }

        OrganizerSubevent organizerSubevent = new OrganizerSubevent(dto.getType(), account, event, subevent);
        organizerSubeventRepository.save(organizerSubevent);

        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditService.logAdmin(jwtUserDetails.getId(), Action.CREATE, ResourceName.ORGANIZERSUBEVENT, organizerSubevent.getId());
        auditService.logAdminUpdate(jwtUserDetails.getId(), ResourceName.SUBEVENT, String.format("Account of email %s associated to the subevent's organization", account.getEmail()), subeventId);
        auditService.logAdminUpdate(jwtUserDetails.getId(), ResourceName.ACCOUNT, String.format("Conta associada à organização do subevento %s", subevent.getTitle()), account.getId());

        return organizerSubevent;
    }

    public List<OrganizerSubevent> findAll(UUID eventId, UUID subeventId) {
        checkEventExists(eventId);
        checkSubeventExists(subeventId);
        return organizerSubeventRepository.findAllBySubeventId(subeventId);
    }

    public List<OrganizerSubEventSiteDto> findAllForSite(UUID subeventId) {
        checkSubeventExists(subeventId);
        return organizerSubeventRepository.findAllOrganizerBySubEventId(subeventId);
    }

    public void delete(UUID eventId, UUID subeventId, UUID organizerSubeventId) {
        OrganizerSubevent organizerSubevent = getOrganizerSubevent(organizerSubeventId);
        checkEventExists(eventId);
        checkSubeventExists(subeventId);

        if(organizerSubevent.getEvent().getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_DELETE_WITH_CANCELED_STATUS);
        }

        if(organizerSubevent.getSubevent().getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ORGANIZER_SUBEVENT_DELETE_WITH_CANCELED_STATUS);
        }

        organizerSubeventRepository.delete(organizerSubevent);
        log.info("Organizer subevent deleted: organizer id={}, subevent id={}", organizerSubeventId, subeventId);

        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditService.logAdminDelete(jwtUserDetails.getId(), ResourceName.ORGANIZERSUBEVENT, organizerSubeventId);
        auditService.logAdminUpdate(jwtUserDetails.getId(), ResourceName.SUBEVENT, String.format("Account of email %s removed from the subevent's organization", organizerSubevent.getAccount().getEmail()), subeventId);
        auditService.logAdminUpdate(jwtUserDetails.getId(), ResourceName.ACCOUNT, String.format("Conta removida da organização do subevento %s", organizerSubevent.getSubevent().getTitle()), organizerSubevent.getAccount().getId());
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
