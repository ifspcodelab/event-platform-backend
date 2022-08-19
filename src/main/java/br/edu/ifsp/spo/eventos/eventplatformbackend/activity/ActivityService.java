package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.dto.CancellationMessageCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.speaker.Speaker;
import br.edu.ifsp.spo.eventos.eventplatformbackend.speaker.SpeakerRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
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
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final EventRepository eventRepository;
    private final SubeventRepository subeventRepository;
    private final ActivitySpeakerRepository activitySpeakerRepository;
    private final SpeakerRepository speakerRepository;
    private final SessionService sessionService;

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
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        Activity activity = new Activity(
                dto.getTitle(),
                dto.getSlug(),
                dto.getDescription(),
                dto.getType(),
                dto.getModality(),
                dto.isNeedRegistration(),
                dto.getDuration(),
                event
        );

        return activityRepository.save(activity);
    }

    public Activity create(UUID eventId, UUID subeventId, ActivityCreateDto dto) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubevent(subeventId);
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
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        Activity activity = new Activity(
                dto.getTitle(),
                dto.getSlug(),
                dto.getDescription(),
                dto.getType(),
                dto.getModality(),
                dto.isNeedRegistration(),
                dto.getDuration(),
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
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        activity.setTitle(dto.getTitle());
        activity.setSlug(dto.getSlug());
        activity.setDescription(dto.getDescription());
        activity.setType(dto.getType());
        activity.setModality(dto.getModality());
        activity.setNeedRegistration(dto.isNeedRegistration());
        activity.setDuration(dto.getDuration());

        return activityRepository.save(activity);
    }

    public Activity update(UUID eventId, UUID subeventId, UUID activityId, ActivityCreateDto dto) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubevent(subeventId);
        Activity activity = getActivity(activityId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);

        if(activityRepository.existsByTitleIgnoreCaseAndSubeventIdAndIdNot(dto.getTitle(), subeventId, activityId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACTIVITY,"title", dto.getTitle());
        }

        if(activityRepository.existsBySlugAndSubeventIdAndIdNot(dto.getSlug(), subeventId, activityId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACTIVITY, "slug", dto.getSlug());
        }

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_CANCELED_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_SUBEVENT_CANCELED_STATUS);
        }

        if(subevent.getExecutionPeriod().getStartDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_EVENT_CANCELED_STATUS);
        }

        if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        activity.setTitle(dto.getTitle());
        activity.setSlug(dto.getSlug());
        activity.setDescription(dto.getDescription());
        activity.setType(dto.getType());
        activity.setModality(dto.getModality());
        activity.setNeedRegistration(dto.isNeedRegistration());
        activity.setDuration(dto.getDuration());

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
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        activity.setStatus(EventStatus.PUBLISHED);
        log.info("Activity published: id={}, title={}", activityId, activity.getTitle());
        return activityRepository.save(activity);
    }

    public Activity publish(UUID eventId, UUID subeventId, UUID activityId) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubevent(subeventId);
        Activity activity = getActivity(activityId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_CANCELED_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_PUBLISHED_STATUS);
        }

        if(subevent.getEvent().getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_SUBEVENT_CANCELED_STATUS);
        }

        if(subevent.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_EVENT_CANCELED_STATUS);
        }

        if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_PUBLISH_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        activity.setStatus(EventStatus.PUBLISHED);
        log.info("Activity published: id={}, title={}", activityId, activity.getTitle());
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

        if(activity.getStatus().equals(EventStatus.PUBLISHED)) {
            if (event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START);
            }
        }

        activity.setStatus(EventStatus.DRAFT);
        log.info("Activity unpublished: id={}, title={}", activityId, activity.getTitle());
        return activityRepository.save(activity);
    }

    public Activity unpublish(UUID eventId, UUID subeventId, UUID activityId) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubevent(subeventId);
        Activity activity = getActivity(activityId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);

        if(activity.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UNPUBLISH_WITH_DRAFT_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UNPUBLISH_WITH_CANCELED_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_SUBEVENT_CANCELED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UNPUBLISH_WITH_EVENT_CANCELED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.PUBLISHED)) {
            if (event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START);
            }
        }

        if(subevent.getExecutionPeriod().getStartDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UNPUBLISH_WITH_SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        activity.setStatus(EventStatus.DRAFT);
        log.info("Activity unpublished: id={}, title={}", activityId, activity.getTitle());
        return activityRepository.save(activity);
    }

    public Activity cancel(UUID eventId, UUID activityId, CancellationMessageCreateDto cancellationMessageCreateDto) {
        Event event = getEvent(eventId);
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_CANCELED_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_DRAFT_STATUS);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_AN_EVENT_WITH_CANCELLED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_AN_EVENT_WITH_DRAFT_STATUS);
        }

        if(event.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_AFTER_EVENT_EXECUTION_PERIOD);
        }

        sessionService.cancelAllByActivityId(eventId, activityId);
        activity.setStatus(EventStatus.CANCELED);
        activity.setCancellationMessage(cancellationMessageCreateDto.getReason());
        log.info("Activity canceled: id={}, title={}", activityId, activity.getTitle());
        return activityRepository.save(activity);
    }

    public Activity cancel(UUID eventId, UUID subeventId, UUID activityId, CancellationMessageCreateDto cancellationMessageCreateDto) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubevent(subeventId);
        Activity activity = getActivity(activityId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_CANCELED_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_DRAFT_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_A_SUBEVENT_WITH_DRAFT_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_A_SUBEVENT_WITH_CANCELED_STATUS);
        }

        if(subevent.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_SUBEVENT_AFTER_EXECUTION_PERIOD);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_AN_EVENT_WITH_CANCELLED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_AN_EVENT_WITH_DRAFT_STATUS);
        }

        sessionService.cancelAllByActivityId(eventId, subeventId, activityId);
        activity.setStatus(EventStatus.CANCELED);
        activity.setCancellationMessage(cancellationMessageCreateDto.getReason());
        log.info("Activity canceled: id={}, title={}", activityId, activity.getTitle());
        return activityRepository.save(activity);
    }

    public List<Activity> findAll(UUID eventId) {
        checksEventExists(eventId);
        return activityRepository.findAllByEventIdAndSubeventNull(eventId);
    }

    public List<Activity> findAll(UUID eventId, UUID subeventId) {
        checksEventExists(eventId);
        checksSubeventExists(subeventId);
        checkIfEventIsAssociateToSubevent(eventId, getSubevent(subeventId));
        return activityRepository.findAllBySubeventId(subeventId);
    }

    public Activity findById(UUID eventId, UUID activityId) {
        Activity activity = getActivity(activityId);
        checksEventExists(eventId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        return activity;
    }

    public Activity findById(UUID eventId, UUID subeventId, UUID activityId) {
        Activity activity = getActivity(activityId);
        checksEventExists(eventId);
        checksSubeventExists(subeventId);
        checkIfEventIsAssociateToSubevent(eventId, getSubevent(subeventId));
        checksIfEventIsAssociateToActivity(eventId, activity);
        return activity;
    }

    public void delete(UUID eventId, UUID activityId) {
        Event event = getEvent(eventId);
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_DELETE_WITH_STATUS_CANCELED);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_DELETE_WITH_EVENT_CANCELED_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.PUBLISHED)) {
            if(event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.ACTIVITY_DELETE_WITH_PUBLISHED_STATUS_AFTER_REGISTRATION_PERIOD_START);
            }
        }

        activityRepository.delete(activity);
        log.info("Activity deleted: id={}, title={}", activityId, activity.getTitle());
    }

    public void delete(UUID eventId, UUID subeventId, UUID activityId) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubevent(subeventId);
        Activity activity = getActivity(activityId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_DELETE_WITH_STATUS_CANCELED);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_DELETE_WITH_SUBEVENT_CANCELED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_DELETE_WITH_EVENT_CANCELED_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.PUBLISHED)) {
            if(event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.ACTIVITY_DELETE_WITH_PUBLISHED_STATUS_AFTER_REGISTRATION_PERIOD_START);
            }
        }

        if(activity.getStatus().equals(EventStatus.PUBLISHED)) {
            if (subevent.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.ACTIVITY_DELETE_WITH_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD);
            }
        }

        activityRepository.delete(activity);
        log.info("Activity deleted: id={}, title={}", activityId, activity.getTitle());
    }

    public ActivitySpeaker addActivityEventSpeaker(UUID eventId, UUID activityId, ActivitySpeakerCreateDto dto) {
        Event event = getEvent(eventId);
        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SPEAKER_ADD_WITH_EVENT_CANCELED_STATUS);
        }

        Activity activity = getActivity(activityId);
        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SPEAKER_ADD_WITH_ACTIVITY_CANCELED_STATUS);
        }

        Speaker speaker = getSpeaker(dto.getSpeakerId());

        ActivitySpeaker activitySpeaker = new ActivitySpeaker(activity, speaker);

        return activitySpeakerRepository.save(activitySpeaker);
    }

    public ActivitySpeaker addActivitySubEventSpeaker(UUID eventId, UUID subeventId, UUID activityId, ActivitySpeakerCreateDto dto) {
        Event event = getEvent(eventId);
        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SPEAKER_ADD_WITH_EVENT_CANCELED_STATUS);
        }

        Subevent subevent = getSubevent(subeventId);
        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SPEAKER_ADD_WITH_SUBEVENT_CANCELED_STATUS);
        }

        Activity activity = getActivity(activityId);
        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SPEAKER_ADD_WITH_ACTIVITY_CANCELED_STATUS);
        }

        Speaker speaker = getSpeaker(dto.getSpeakerId());

        ActivitySpeaker activitySpeaker = new ActivitySpeaker(activity, speaker);

        return activitySpeakerRepository.save(activitySpeaker);
    }

    public void deleteActivityEventSpeaker(UUID eventId, UUID activityId, UUID activitySpeakerId) {
        Event event = getEvent(eventId);
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        activitySpeakerRepository.deleteById(activitySpeakerId);
        log.info("Activity Speaker deleted: id={}, title={}", activityId, activity.getTitle());
    }

    public void deleteActivitySubEventSpeaker(UUID eventId, UUID subeventId, UUID activityId, UUID activitySpeakerId) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubevent(subeventId);
        Activity activity = getActivity(activityId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        activitySpeakerRepository.deleteById(activitySpeakerId);
        log.info("Activity Speaker deleted: id={}, title={}", activityId, activity.getTitle());
    }

    public List<ActivitySpeaker> findAllActivityEventSpeaker(UUID eventId, UUID activityId) {
        checksEventExists(eventId);
        return activitySpeakerRepository.findAllByActivityId(activityId);
    }

    public List<ActivitySpeaker> findAllActivitySubEventSpeaker(UUID eventId, UUID subeventId, UUID activityId) {
        checksEventExists(eventId);
        checksSubeventExists(subeventId);
        return activitySpeakerRepository.findAllByActivityId(activityId);
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
    }

    private Subevent getSubevent(UUID subeventId) {
        return subeventRepository.findById(subeventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SUBEVENT, subeventId));
    }

    private Speaker getSpeaker(UUID speakerId) {
        return speakerRepository.findById(speakerId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SPEAKER, speakerId));
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
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_TO_EVENT);
        }
    }

    private void checksIfSubeventIsAssociateToActivity(UUID subeventId, Activity activity) {
        if (!activity.getSubevent().getId().equals(subeventId)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_TO_SUBEVENT);
        }
    }

    private void checksEventExists(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(ResourceName.EVENT, eventId);
        }
    }

    private void checksSubeventExists(UUID subeventId) {
        if(!subeventRepository.existsById(subeventId)) {
            throw new ResourceNotFoundException(ResourceName.EVENT, subeventId);
        }
    }

    @Transactional
    public void cancelAllByEventId(UUID eventId) {

        List<Activity> activities = new ArrayList<>();
        for (Activity activity : this.findAll(eventId)) {

            if(activity.getStatus().equals(EventStatus.PUBLISHED) &&
                    (activity.getEvent().getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
                            activity.getEvent().getRegistrationPeriod().getStartDate().isEqual(LocalDate.now()))
            ) {
                activity.setStatus(EventStatus.CANCELED);
                activities.add(activity);
            }

            sessionService.cancelAllByActivityId(eventId, activity.getId());

        }

        activityRepository.saveAll(activities);
    }

    @Transactional
    public void cancelAllBySubeventId(UUID eventId, UUID subeventId) {

        List<Activity> activities = new ArrayList<>();
        for (Activity activity : this.findAll(eventId, subeventId)) {

            if(activity.getStatus().equals(EventStatus.PUBLISHED) &&
                    (activity.getEvent().getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
                            activity.getEvent().getRegistrationPeriod().getStartDate().isEqual(LocalDate.now())) &&
                    (activity.getSubevent().getExecutionPeriod().getEndDate().isAfter(LocalDate.now()) ||
                            activity.getSubevent().getExecutionPeriod().getEndDate().isEqual(LocalDate.now()))
            ) {
                activity.setStatus(EventStatus.CANCELED);
                activities.add(activity);
            }

            sessionService.cancelAllByActivityId(eventId, subeventId, activity.getId());
        }

        activityRepository.saveAll(activities);
    }
}
