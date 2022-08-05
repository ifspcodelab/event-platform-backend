package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final EventRepository eventRepository;
    private final SubeventRepository subeventRepository;

    public Activity create(UUID eventId, ActivityCreateDto dto) {
        Event event = getEvent(eventId);

        if(activityRepository.existsByTitleIgnoreCaseAndEventId(dto.getTitle(), eventId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACTIVITY, "title", dto.getTitle());
        }

        if(activityRepository.existsBySlugAndEventId(dto.getSlug(), eventId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACTIVITY, "slug", dto.getSlug());
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CREATE_WITH_EVENT_CANCELED_STATUS);
        }

        if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        } // MUDAR ESSA VALIDAÇÃO?

        Activity activity = new Activity(
                dto.getTitle(),
                dto.getSlug(),
                dto.getDescription(),
                dto.getActivityType(),
                dto.isOnline(),
                dto.isNeedRegistration(),
                event
        );

        return activityRepository.save(activity);
    }

    public Activity create(UUID eventId, UUID subeventId, ActivityCreateDto dto) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubEvent(subeventId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);

        if(activityRepository.existsByTitleIgnoreCaseAndSubeventId(dto.getTitle(), subeventId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACTIVITY, "title", dto.getTitle());
        }

        if(activityRepository.existsBySlugAndSubeventId(dto.getSlug(), subeventId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACTIVITY, "slug", dto.getSlug());
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CREATE_WITH_EVENT_CANCELED_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CREATE_WITH_SUBEVENT_CANCELED_STATUS);
        }

        if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        } // MUDAR ESSA VALIDAÇÃO?

        Activity activity = new Activity(
                dto.getTitle(),
                dto.getSlug(),
                dto.getDescription(),
                dto.getActivityType(),
                dto.isOnline(),
                dto.isNeedRegistration(),
                event,
                subevent
        );

        return activityRepository.save(activity);
    }

    public Activity update(UUID eventId, UUID activityId, ActivityCreateDto dto) {
        Event event = getEvent(eventId);
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);

        if(activityRepository.existsByTitleIgnoreCaseAndEventIdAndIdNot(dto.getTitle(), eventId, activityId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACTIVITY,"title", dto.getTitle());
        }

        if(activityRepository.existsBySlugAndEventIdAndIdNot(dto.getSlug(), eventId, activityId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACTIVITY, "slug", dto.getSlug());
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_EVENT_CANCELED_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_CANCELED_STATUS);
        }

        if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        activity.setTitle(dto.getTitle());
        activity.setSlug(dto.getSlug());
        activity.setDescription(dto.getDescription());
        activity.setType(dto.getActivityType());
        activity.setOnline(dto.isOnline());
        activity.setNeedRegistration(dto.isNeedRegistration());

        return activityRepository.save(activity);
    }

    public Activity update(UUID eventId, UUID subeventId, UUID activityId, ActivityCreateDto dto) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubEvent(subeventId);
        Activity activity = getActivity(activityId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);

        if(activityRepository.existsByTitleIgnoreCaseAndSubeventIdAndIdNot(dto.getTitle(), subeventId, activityId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACTIVITY,"title", dto.getTitle());
        }

        if(activityRepository.existsBySlugAndSubeventIdAndIdNot(dto.getSlug(), subeventId, activityId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACTIVITY, "slug", dto.getSlug());
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_EVENT_CANCELED_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_SUBEVENT_CANCELED_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_CANCELED_STATUS);
        }

        if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        activity.setTitle(dto.getTitle());
        activity.setSlug(dto.getSlug());
        activity.setDescription(dto.getDescription());
        activity.setType(dto.getActivityType());
        activity.setOnline(dto.isOnline());
        activity.setNeedRegistration(dto.isNeedRegistration());

        return activityRepository.save(activity);
    }

    public Activity publish(UUID eventId, UUID activityId) {
        Event event = getEvent(eventId);
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);

        if(activity.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_PUBLISHED_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_CANCELED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_EVENT_CANCELED_STATUS);
        }

        if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        activity.setStatus(EventStatus.PUBLISHED);
        return activityRepository.save(activity);
    }

    public Activity publish(UUID eventId, UUID subeventId, UUID activityId) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubEvent(subeventId);
        Activity activity = getActivity(activityId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);


        if(activity.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_PUBLISHED_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_CANCELED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_EVENT_CANCELED_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_SUBEVENT_CANCELED_STATUS);
        }

        if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        activity.setStatus(EventStatus.PUBLISHED);
        return activityRepository.save(activity);
    }

    public Activity unpublish(UUID eventId, UUID activityId) {
        Event event = getEvent(eventId);
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);

        if(activity.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UNPUBLISH_WITH_DRAFT_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UNPUBLISH_WITH_CANCELED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UNPUBLISH_WITH_EVENT_CANCELED_STATUS);
        }

        if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        activity.setStatus(EventStatus.DRAFT);
        return activityRepository.save(activity);
    }

    public Activity unpublish(UUID eventId, UUID subeventId, UUID activityId) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubEvent(subeventId);
        Activity activity = getActivity(activityId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);

        if(activity.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UNPUBLISH_WITH_DRAFT_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UNPUBLISH_WITH_CANCELED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UNPUBLISH_WITH_EVENT_CANCELED_STATUS);
        }

        if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_SUBEVENT_CANCELED_STATUS);
        }

        activity.setStatus(EventStatus.DRAFT);
        return activityRepository.save(activity);
    }

    public Activity cancel(UUID eventId, UUID activityId) {
        Event event = getEvent(eventId);
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_CANCEL_STATUS);
        }
        if(activity.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_DRAFT_STATUS);
        }

        if(event.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        activity.setStatus(EventStatus.CANCELED);
        return activityRepository.save(activity);
    }

    public List<Activity> findALl(UUID eventId) {
        checksEventExists(eventId);
        return activityRepository.findAllByEventId(eventId);
    }

    public Activity findById(UUID eventId, UUID activityId) {
        Activity activity = getActivity(activityId);
        checksEventExists(eventId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        return activity;
    }

    public void delete(UUID eventId, UUID activityId) { // TODO VERIFICAR MELHOR AS VALIDAÇÕES
        Event event = getEvent(eventId);
        checksEventExists(eventId);
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_DELETE_WITH_STATUS_CANCELED);
        }

        if(event.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        if(event.getStatus().equals(EventStatus.PUBLISHED) &&
                event.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_DELETE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD);
        }

        if(event.getStatus().equals(EventStatus.PUBLISHED) &&
           event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_PERIOD_BEFORE_TODAY); // UTILIZAR OUTRA BUSINESS RULE
        }

        activityRepository.delete(activity);
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
    }

    private Subevent getSubEvent(UUID subeventId) {
        return subeventRepository.findById(subeventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SUBEVENT, subeventId));
    }

    private void checkIfEventIsAssociateToSubevent(UUID eventId, Subevent subevent) {
        if (!subevent.getEvent().getId().equals(eventId)) {
            throw new ResourceReferentialIntegrityException(ResourceName.SUBEVENT, ResourceName.EVENT);
        }
    }

    private Activity getActivity(UUID activityId) {
        return activityRepository.findById(activityId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACTIVITY, activityId));
    }

    private void checksIfEventIsAssociateToActivity(UUID eventId, Activity activity) {
        if (!activity.getEvent().getId().equals(eventId)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_EVENT);
        }
    }

    private void checksIfSubeventIsAssociateToActivity(UUID subeventId, Activity activity) {
        if (!activity.getSubevent().getId().equals(subeventId)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_EVENT);
        }
    }

    private void checksEventExists(UUID eventId) {
        if(!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(ResourceName.EVENT, eventId);
        }
    }
}
