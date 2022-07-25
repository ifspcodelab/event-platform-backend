package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class SubeventService {
    private final SubeventRepository subeventRepository;
    private final EventRepository eventRepository;

    public Subevent create(SubeventCreateDto dto, UUID eventId) {
        Event event = getEvent(eventId);

        if(subeventRepository.existsByTitleAndEvent(dto.getTitle(), event)) {
            throw new ResourceAlreadyExistsException(ResourceName.SUBEVENT,"title", dto.getTitle());
        }

        if(subeventRepository.existsBySlugAndEvent(dto.getSlug(), event)) {
            throw new ResourceAlreadyExistsException(ResourceName.SUBEVENT, "slug", dto.getSlug());
        }

        if(dto.getExecutionPeriod().getStartDate().isBefore(LocalDate.now()) ||
            dto.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        if(dto.getExecutionPeriod().getStartDate().isBefore(event.getExecutionPeriod().getStartDate())) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_BEFORE_EVENT);
        }

        if(dto.getExecutionPeriod().getEndDate().isAfter(event.getExecutionPeriod().getEndDate())) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_AFTER_EVENT);
        }

        Subevent subevent = new Subevent(
                dto.getTitle(),
                dto.getSlug(),
                dto.getSummary(),
                dto.getPresentation(),
                dto.getExecutionPeriod(),
                dto.getSmallerImage(),
                dto.getBiggerImage(),
                event
        );
        return subeventRepository.save(subevent);
    }

    public Subevent findById(UUID eventId, UUID subeventId) {
        Subevent subevent = getSubevent(subeventId);

        checksIfSubeventIsAssociateToEvent(subevent,eventId);

        return subevent;
    }

    public List<Subevent> findAll(UUID eventId) {
        checksEventExists(eventId);

        return subeventRepository.findAllByEventId(eventId);
    }

    public void delete(UUID eventId, UUID subeventId) {
        Subevent subevent = getSubevent(subeventId);
        Event event = getEvent(eventId);

        checksIfSubeventIsAssociateToEvent(subevent, eventId);

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_DELETE_WITH_STATUS_CANCELED);
        }

        if(subevent.getStatus().equals(EventStatus.PUBLISHED) &&
                subevent.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_DELETE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD);
        }

        if(subevent.getStatus().equals(EventStatus.PUBLISHED) &&
                event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
                event.getRegistrationPeriod().getStartDate().isEqual(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_WITH_PUBLISHED_STATUS_DELETE_IN_REGISTRATION_PERIOD);
        }

        subeventRepository.deleteById(subeventId);
        log.info("Subevent deleted: id={}, title={}", subeventId, subevent.getTitle());
    }

    public Subevent update(UUID eventId, UUID subeventId, SubeventCreateDto dto) {
        Subevent subevent = getSubevent(subeventId);
        Event event = getEvent(eventId);
        checksIfSubeventIsAssociateToEvent(subevent, eventId);

        if(subeventRepository.existsByTitleAndEvent(dto.getTitle(), event)) {
            throw new ResourceAlreadyExistsException(ResourceName.SUBEVENT,"title", dto.getTitle());
        }

        if(subeventRepository.existsBySlugAndEvent(dto.getSlug(), event)) {
            throw new ResourceAlreadyExistsException(ResourceName.SUBEVENT, "slug", dto.getSlug());
        }

        if(dto.getExecutionPeriod().getStartDate().isBefore(LocalDate.now()) ||
            dto.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        if(dto.getExecutionPeriod().getStartDate().isBefore(event.getExecutionPeriod().getStartDate())) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_BEFORE_EVENT);
        }

        if(dto.getExecutionPeriod().getEndDate().isAfter(event.getExecutionPeriod().getEndDate())) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_AFTER_EVENT);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_UPDATE_WITH_CANCELED_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.PUBLISHED) &&
                subevent.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_UPDATE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD);
        }

        subevent.setTitle(dto.getTitle());
        subevent.setSlug(dto.getSlug());
        subevent.setSummary(dto.getSummary());
        subevent.setPresentation(dto.getPresentation());
        subevent.setExecutionPeriod(dto.getExecutionPeriod());
        subevent.setSmallerImage(dto.getSmallerImage());
        subevent.setBiggerImage(dto.getBiggerImage());

        return subeventRepository.save(subevent);
    }

    public Subevent cancel(UUID eventId, UUID subeventId) {
        Subevent subevent = getSubevent(subeventId);
        checksIfSubeventIsAssociateToEvent(subevent, eventId);

        if(subevent.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_CANCEL_WITH_DRAFT_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.PUBLISHED)) {
            if(subevent.getEvent().getRegistrationPeriod().getStartDate().isAfter(LocalDate.now())
            ) {
                throw new BusinessRuleException(BusinessRuleType.SUBEVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_NOT_START);
            }

            if(subevent.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.SUBEVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_EXECUTION_PERIOD_END);
            }
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_CANCEL_WITH_CANCELED_STATUS);
        }

        subevent.setStatus(EventStatus.CANCELED);
        log.info("Subevent canceled: id={}, title={}", subeventId, subevent.getTitle());

        return subeventRepository.save(subevent);
    }

    public Subevent publish(UUID eventId, UUID subeventId) {
        Subevent subevent = getSubevent(subeventId);
        checksIfSubeventIsAssociateToEvent(subevent, eventId);

        if(subevent.getStatus().equals(EventStatus.DRAFT)) {
            if(subevent.getEvent().getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
                    subevent.getEvent().getRegistrationPeriod().getStartDate().isEqual(LocalDate.now())
            ) {
                throw new BusinessRuleException(BusinessRuleType.SUBEVENT_PUBLISH_WITH_DRAFT_STATUS_AND_REGISTRATION_PERIOD_START);
            }
        }

        if(subevent.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_PUBLISH_WITH_PUBLISHED_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_PUBLISH_WITH_CANCELED_STATUS);
        }

        subevent.setStatus(EventStatus.PUBLISHED);

        log.info("Subevent published: id={}, title={}", subeventId, subevent.getTitle());
        return subeventRepository.save(subevent);
    }

    public Subevent unpublish(UUID eventId, UUID subeventId) {
        Subevent subevent = getSubevent(subeventId);
        checksIfSubeventIsAssociateToEvent(subevent, eventId);

        if(subevent.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_UNPUBLISH_WITH_DRAFT_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.PUBLISHED)) {
            if(subevent.getEvent().getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
                    subevent.getEvent().getRegistrationPeriod().getStartDate().isEqual(LocalDate.now())
            ) {
                throw new BusinessRuleException(BusinessRuleType.SUBEVENT_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START);
            }
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_UNPUBLISH_WITH_CANCELED_STATUS);
        }

        subevent.setStatus(EventStatus.DRAFT);

        log.info("Subevent unpublished: id={}, title={}", subeventId, subevent.getTitle());
        return subeventRepository.save(subevent);
    }

    @Transactional
    public void cancelAllByEventId(UUID eventId) {
        getEvent(eventId);

        List<Subevent> subevents = new ArrayList<>();
        for (Subevent subevent : this.findAll(eventId)) {

            if(subevent.getStatus().equals(EventStatus.PUBLISHED) &&
                    (subevent.getEvent().getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
                    subevent.getEvent().getRegistrationPeriod().getStartDate().isEqual(LocalDate.now())) &&
                    (subevent.getExecutionPeriod().getEndDate().isAfter(LocalDate.now()) ||
                            subevent.getExecutionPeriod().getEndDate().isEqual(LocalDate.now()))
            ) {
                subevent.setStatus(EventStatus.CANCELED);
                subevents.add(subevent);
            }
        }
        subeventRepository.saveAll(subevents);
    }

    @Transactional
    public void unpublishAllByEventId(UUID eventId) {
        getEvent(eventId);
        List<Subevent> subevents = new ArrayList<>();

        for (Subevent subevent : this.findAll(eventId)) {

            if(subevent.getStatus().equals(EventStatus.PUBLISHED) &&
                    subevent.getEvent().getRegistrationPeriod().getStartDate().isAfter(LocalDate.now())
            ) {
                subevent.setStatus(EventStatus.DRAFT);
                subevents.add(subevent);
            }

        }
        subeventRepository.saveAll(subevents);
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
    }

    private Subevent getSubevent(UUID subeventId) {
        return subeventRepository.findById(subeventId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SUBEVENT, subeventId));
    }

    private void checksEventExists(UUID eventId) {
        if(!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(ResourceName.EVENT, eventId);
        }
    }

    private void checksIfSubeventIsAssociateToEvent(Subevent subevent, UUID eventId) {
        if (!subevent.getEvent().getId().equals(eventId)) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_IS_NOT_ASSOCIATED_EVENT);
        }
    }
}
