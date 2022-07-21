package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.BusinessRuleException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.BusinessRuleType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
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

    public Event create(EventCreateDto dto) {
        if(eventRepository.existsByTitle(dto.getTitle())) {
            throw new ResourceAlreadyExistsException("event", "title", dto.getTitle());
        }

        if(eventRepository.existsBySlug(dto.getSlug())) {
            throw new ResourceAlreadyExistsException("event", "slug", dto.getSlug());
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
            throw new BusinessRuleException(BusinessRuleType.EVENT_DELETE_WITH_STATUS_CANCELED);
        }

        if(event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
                event.getRegistrationPeriod().getStartDate().isEqual(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_DELETE_IN_PERIOD_REGISTRATION_START);
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
            throw new ResourceAlreadyExistsException("event", "title", dto.getTitle());
        }

        if(eventRepository.existsBySlug(dto.getSlug())) {
            throw new ResourceAlreadyExistsException("event", "slug", dto.getSlug());
        }

        if(dto.getRegistrationPeriod().getStartDate().isAfter(dto.getExecutionPeriod().getStartDate())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_START_AFTER_EVENT_EXECUTION_START);
        }

        if(dto.getRegistrationPeriod().getEndDate().isAfter(dto.getExecutionPeriod().getEndDate())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_END_AFTER_EVENT_EXECUTION_END);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_UPDATE_WITH_STATUS_CANCELED);
        }

        if(event.getExecutionPeriod().getEndDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_UPDATE_AFTER_PERIOD_EXECUTION_END);
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

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("event", eventId));
    }
}
