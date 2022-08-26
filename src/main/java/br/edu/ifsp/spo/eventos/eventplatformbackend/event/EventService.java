package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.Action;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.dto.CancellationMessageCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.DiffResult;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final SubeventRepository subeventRepository;
    private final SubeventService subeventService;
    private final AuditService auditService;

    public Event create(EventCreateDto dto) {
        if(eventRepository.existsByTitle(dto.getTitle())) {
            throw new ResourceAlreadyExistsException(ResourceName.EVENT, "title", dto.getTitle());
        }

        if(eventRepository.existsBySlug(dto.getSlug())) {
            throw new ResourceAlreadyExistsException(ResourceName.EVENT, "slug", dto.getSlug());
        }

        if(dto.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
            dto.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        if(dto.getExecutionPeriod().getStartDate().isBefore(LocalDate.now()) ||
            dto.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        if(dto.getRegistrationPeriod().getStartDate().isAfter(dto.getExecutionPeriod().getStartDate())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_START_AFTER_EVENT_EXECUTION_START);
        }

        if(dto.getRegistrationPeriod().getEndDate().isAfter(dto.getExecutionPeriod().getEndDate())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_END_AFTER_EVENT_EXECUTION_END);
        }

        Event event = new Event(
            dto.getTitle(),
            dto.getSlug(),
            dto.getSummary(),
            dto.getPresentation(),
            dto.getContact(),
            dto.getRegistrationPeriod(),
            dto.getExecutionPeriod(),
            dto.getSmallerImage(),
            dto.getBiggerImage()
        );

        return eventRepository.save(event);
    }

    public Event findById(UUID eventId) {
        return getEvent(eventId);
    }

    public Event findBySlug(String slug) {
        return  eventRepository.findEventBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, slug));
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public void delete(UUID eventId, UUID accountId) {
        Event event = getEvent(eventId);

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_DELETE_WITH_CANCELED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.PUBLISHED)) {
            if(event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
                event.getRegistrationPeriod().getStartDate().isEqual(LocalDate.now())
            ) {
                throw new BusinessRuleException(BusinessRuleType.EVENT_DELETE_WITH_PUBLISHED_STATUS_AFTER_REGISTRATION_PERIOD_START);
            }
        }

        if(subeventRepository.existsByEventId(eventId)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_DELETE_WITH_SUBEVENTS);
        }

        eventRepository.deleteById(eventId);

        auditService.logAdminDelete(accountId, ResourceName.EVENT, eventId);

        log.info("Event deleted: id={}, title={}", eventId, event.getTitle());
    }

    public Event update(UUID eventId, EventCreateDto dto, UUID accountId) {
        Event event = getEvent(eventId);

        if(event.getStatus().equals(EventStatus.PUBLISHED) &&
            event.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_UPDATE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD);
        }

        if(eventRepository.existsByTitleAndIdNot(dto.getTitle(), eventId)) {
            throw new ResourceAlreadyExistsException(ResourceName.EVENT, "title", dto.getTitle());
        }

        if(eventRepository.existsBySlugAndIdNot(dto.getSlug(), eventId)) {
            throw new ResourceAlreadyExistsException(ResourceName.EVENT, "slug", dto.getSlug());
        }

        if(dto.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
            dto.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        if(dto.getExecutionPeriod().getStartDate().isBefore(LocalDate.now()) ||
            dto.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        if(dto.getRegistrationPeriod().getStartDate().isAfter(dto.getExecutionPeriod().getStartDate())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_START_AFTER_EVENT_EXECUTION_START);
        }

        if(dto.getRegistrationPeriod().getEndDate().isAfter(dto.getExecutionPeriod().getEndDate())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_END_AFTER_EVENT_EXECUTION_END);
        }

        if(event.getStatus().equals(EventStatus.PUBLISHED)) {
            if(event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
                event.getRegistrationPeriod().getStartDate().isEqual(LocalDate.now())
            ) {
                if(!dto.getSlug().equals(event.getSlug())) {
                    throw new BusinessRuleException(BusinessRuleType.EVENT_UPDATE_WITH_PUBLISHED_STATUS_AND_MODIFIED_SLUG_AFTER_RERISTRATION_PERIOD_START);
                }

                if(!dto.getRegistrationPeriod().getStartDate().isEqual(event.getRegistrationPeriod().getStartDate())) {
                    throw new BusinessRuleException(BusinessRuleType.EVENT_UPDATE_WITH_PUBLISHED_STATUS_AND_RERISTRATION_PERIOD_START_MODIFIED_AFTER_RERISTRATION_PERIOD_START);
                }

                if(!dto.getExecutionPeriod().getStartDate().isEqual(event.getExecutionPeriod().getStartDate())) {
                    throw new BusinessRuleException(BusinessRuleType.EVENT_UPDATE_WITH_PUBLISHED_STATUS_AND_EXECUTION_PERIOD_START_MODIFIED_AFTER_RERISTRATION_PERIOD_START);
                }
            }
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_UPDATE_WITH_CANCELED_STATUS);
        }

        Event currentEvent = new Event();
        currentEvent.setTitle(event.getTitle());
        currentEvent.setSlug(event.getSlug());
        currentEvent.setSummary(event.getSummary());
        currentEvent.setPresentation(event.getPresentation());
        currentEvent.setContact(event.getContact());
        currentEvent.setRegistrationPeriod(event.getRegistrationPeriod());
        currentEvent.setExecutionPeriod(event.getExecutionPeriod());
        currentEvent.setSmallerImage(event.getSmallerImage());
        currentEvent.setBiggerImage(event.getBiggerImage());

        event.setTitle(dto.getTitle());
        event.setSlug(dto.getSlug());
        event.setSummary(dto.getSummary());
        event.setPresentation(dto.getPresentation());
        event.setContact(dto.getContact());
        event.setRegistrationPeriod(dto.getRegistrationPeriod());
        event.setExecutionPeriod(dto.getExecutionPeriod());
        event.setSmallerImage(dto.getSmallerImage());
        event.setBiggerImage(dto.getBiggerImage());

        DiffResult<?> diffResult = currentEvent.diff(event);

        auditService.logAdminUpdate(accountId, ResourceName.EVENT, diffResult.getDiffs().toString(), eventId);

        return eventRepository.save(event);
    }

    public Event cancel(UUID eventId, CancellationMessageCreateDto cancellationMessageCreateDto, UUID accountId) {
        Event event = getEvent(eventId);

        if(event.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_CANCEL_WITH_DRAFT_STATUS);
        }

        if(event.getStatus().equals(EventStatus.PUBLISHED)) {
            if(event.getRegistrationPeriod().getStartDate().isAfter(LocalDate.now())) {
                throw new BusinessRuleException(
                    BusinessRuleType.EVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_NOT_START
                );
            }

            if(event.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(
                    BusinessRuleType.EVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_EXECUTION_PERIOD_END
                );
            }
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_CANCEL_WITH_CANCELED_STATUS);
        }

        event.setStatus(EventStatus.CANCELED);
        event.setCancellationMessage(cancellationMessageCreateDto.getReason());
        subeventService.cancelAllByEventId(eventId);

        log.info("Event canceled: id={}, title={}", eventId, event.getTitle());

        auditService.logAdmin(accountId, Action.CANCEL, ResourceName.EVENT, eventId);

        return eventRepository.save(event);
    }

    public Event publish(UUID eventId, UUID accountId) {
        Event event = getEvent(eventId);

        if(event.getStatus().equals(EventStatus.DRAFT)) {
            if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.EVENT_PUBLISH_WITH_DRAFT_STATUS_AND_REGISTRATION_PERIOD_END);
            }
        }

        if(event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_PUBLISH_WITH_PUBLISHED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_PUBLISH_WITH_CANCELED_STATUS);
        }

        event.setStatus(EventStatus.PUBLISHED);

        log.info("Event published: id={}, title={}", eventId, event.getTitle());

        auditService.logAdmin(accountId, Action.PUBLISH, ResourceName.EVENT, eventId);

        return eventRepository.save(event);
    }

    public Event unpublish(UUID eventId, UUID accountId) {
        Event event = getEvent(eventId);

        if(event.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_UNPUBLISH_WITH_DRAFT_STATUS);
        }

        if(event.getStatus().equals(EventStatus.PUBLISHED)) {
            if(event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
                event.getRegistrationPeriod().getStartDate().equals(LocalDate.now())
            ) {
                throw new BusinessRuleException(
                    BusinessRuleType.EVENT_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START
                );
            }
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_UNPUBLISH_WITH_CANCELED_STATUS);
        }

        event.setStatus(EventStatus.DRAFT);
        subeventService.unpublishAllByEventId(eventId);

        log.info("Event unpublished: id={}, title={}", eventId, event.getTitle());

        auditService.logAdmin(accountId, Action.UNPUBLISH, ResourceName.EVENT, eventId);

        return eventRepository.save(event);
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
    }
}
