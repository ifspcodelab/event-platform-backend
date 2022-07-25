package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public void delete(UUID eventId) {
        Event event = getEvent(eventId);

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_DELETE_WITH_CANCELED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.PUBLISHED) &&
            event.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_DELETE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD);
        }

        if(event.getStatus().equals(EventStatus.PUBLISHED) &&
            event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
            event.getRegistrationPeriod().getStartDate().isEqual(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_DELETE_WITH_PUBLISHED_STATUS_IN_REGISTRATION_PERIOD);
        }

        if(subeventRepository.existsByEventId(eventId)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_DELETE_WITH_SUBEVENTS);
        }

        eventRepository.deleteById(eventId);

        log.info("Event deleted: id={}, title={}", eventId, event.getTitle());
    }

    public Event update(UUID eventId, EventCreateDto dto) {
        Event event = getEvent(eventId);

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

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_UPDATE_WITH_CANCELED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.PUBLISHED) &&
            event.getExecutionPeriod().getEndDate().isAfter(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_UPDATE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD);
        }

        event.setTitle(dto.getTitle());
        event.setSlug(dto.getSlug());
        event.setSummary(dto.getSummary());
        event.setPresentation(dto.getPresentation());
        event.setRegistrationPeriod(dto.getRegistrationPeriod());
        event.setExecutionPeriod(dto.getExecutionPeriod());
        event.setSmallerImage(dto.getSmallerImage());
        event.setBiggerImage(dto.getBiggerImage());

        return eventRepository.save(event);
    }

    public Event cancel(UUID eventId) {
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
        subeventService.cancelAllByEventId(eventId);

        log.info("Event canceled: id={}, title={}", eventId, event.getTitle());

        return eventRepository.save(event);
    }

    public Event publish(UUID eventId) {
        Event event = getEvent(eventId);

        if(event.getStatus().equals(EventStatus.DRAFT)) {
            if(event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(
                    BusinessRuleType.EVENT_PUBLISH_WITH_DRAFT_STATUS_AND_REGISTRATION_PERIOD_START
                );
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

        return eventRepository.save(event);
    }

    public Event unpublish(UUID eventId) {
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

        return eventRepository.save(event);
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
    }
}
