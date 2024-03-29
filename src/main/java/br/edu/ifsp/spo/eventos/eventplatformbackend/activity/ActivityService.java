package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.Action;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.dto.CancellationMessageCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.ActivitySiteDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.speaker.Speaker;
import br.edu.ifsp.spo.eventos.eventplatformbackend.speaker.SpeakerRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.DiffResult;
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
    private final AuditService auditService;

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

        if(event.isExecutionPeriodEnded()) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CREATE_WITH_EVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        if(dto.isNeedRegistration()) {
            if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
            }
        }

        Activity activity = new Activity(
            dto.getTitle(),
            dto.getSlug(),
            dto.getDescription(),
            dto.getType(),
            dto.getModality(),
            dto.isNeedRegistration(),
            dto.getDuration(),
            dto.getSetupTime(),
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

        if(event.isExecutionPeriodEnded()) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CREATE_WITH_EVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CREATE_WITH_SUBEVENT_CANCELED_STATUS);
        }

        if(subevent.isExecutionPeriodEnded()) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CREATE_WITH_EVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        if(dto.isNeedRegistration()) {
            if(event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
            }
        }

        Activity activity = new Activity(
            dto.getTitle(),
            dto.getSlug(),
            dto.getDescription(),
            dto.getType(),
            dto.getModality(),
            dto.isNeedRegistration(),
            dto.getDuration(),
            dto.getSetupTime(),
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

        Activity currentActivity = new Activity();
        currentActivity.setTitle(activity.getTitle());
        currentActivity.setSlug(activity.getSlug());
        currentActivity.setDescription(activity.getDescription());
        currentActivity.setType(activity.getType());
        currentActivity.setModality(activity.getModality());
        currentActivity.setNeedRegistration(activity.isNeedRegistration());
        currentActivity.setDuration(activity.getDuration());
        currentActivity.setSetupTime(activity.getSetupTime());

        activity.setTitle(dto.getTitle());
        activity.setSlug(dto.getSlug());
        activity.setDescription(dto.getDescription());
        activity.setType(dto.getType());
        activity.setModality(dto.getModality());
        activity.setNeedRegistration(dto.isNeedRegistration());
        activity.setDuration(dto.getDuration());
        activity.setSetupTime(dto.getSetupTime());

        DiffResult<?> diffResult = currentActivity.diff(activity);

        auditService.logAdminUpdate(ResourceName.ACTIVITY, diffResult.getDiffs().toString(), activityId);

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

        if (event.getStatus().equals(EventStatus.PUBLISHED)) {
            if (event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
                    event.getRegistrationPeriod().getStartDate().isEqual(LocalDate.now())
            ) {
                if (!dto.getSlug().equals(activity.getSlug())) {
                    throw new BusinessRuleException(BusinessRuleType.ACTIVITY_UPDATE_WITH_EVENT_PUBLISHED_STATUS_AND_MODIFIED_SLUG_AFTER_RERISTRATION_PERIOD_START);
                }
            }
        }

        Activity currentActivity = new Activity();
        currentActivity.setTitle(activity.getTitle());
        currentActivity.setSlug(activity.getSlug());
        currentActivity.setDescription(activity.getDescription());
        currentActivity.setType(activity.getType());
        currentActivity.setModality(activity.getModality());
        currentActivity.setNeedRegistration(activity.isNeedRegistration());
        currentActivity.setDuration(activity.getDuration());
        currentActivity.setSetupTime(activity.getSetupTime());

        activity.setTitle(dto.getTitle());
        activity.setSlug(dto.getSlug());
        activity.setDescription(dto.getDescription());
        activity.setType(dto.getType());
        activity.setModality(dto.getModality());
        activity.setNeedRegistration(dto.isNeedRegistration());
        activity.setDuration(dto.getDuration());
        activity.setSetupTime(dto.getSetupTime());

        DiffResult<?> diffResult = currentActivity.diff(activity);

        auditService.logAdminUpdate(ResourceName.ACTIVITY, diffResult.getDiffs().toString(), activityId);

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
        auditService.logAdmin(Action.PUBLISH, ResourceName.ACTIVITY, activityId);
        return activityRepository.save(activity);
    }

    public Activity publish(UUID eventId, UUID subeventId, UUID activityId) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubEvent(subeventId);
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
        auditService.logAdmin(Action.PUBLISH, ResourceName.ACTIVITY, activityId);
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
        auditService.logAdmin(Action.UNPUBLISH, ResourceName.ACTIVITY, activityId);
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
        auditService.logAdmin(Action.UNPUBLISH, ResourceName.ACTIVITY, activityId);
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

        if(activity.isPublished() && activity.getEvent().isRegistrationPeriodNotStart()) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_DOESNT_START);
        }

        sessionService.cancelAllByActivityId(eventId, activityId, cancellationMessageCreateDto.getReason());
        activity.setStatus(EventStatus.CANCELED);
        activity.setCancellationMessage(cancellationMessageCreateDto.getReason());
        log.info("Activity canceled: id={}, title={}", activityId, activity.getTitle());
        auditService.logAdmin(Action.CANCEL, ResourceName.ACTIVITY, activityId);
        return activityRepository.save(activity);
    }

    public Activity cancel(UUID eventId, UUID subeventId, UUID activityId, CancellationMessageCreateDto cancellationMessageCreateDto) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubEvent(subeventId);
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

        if(activity.isPublished() && activity.getEvent().isRegistrationPeriodNotStart()) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CANCEL_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_DOESNT_START);
        }

        sessionService.cancelAllByActivityId(eventId, subeventId, activityId, cancellationMessageCreateDto.getReason());
        activity.setStatus(EventStatus.CANCELED);
        activity.setCancellationMessage(cancellationMessageCreateDto.getReason());
        log.info("Activity canceled: id={}, title={}", activityId, activity.getTitle());
        auditService.logAdmin(Action.CANCEL, ResourceName.ACTIVITY, activityId);
        return activityRepository.save(activity);
    }

    public List<Activity> findAll(UUID eventId) {
        checksEventExists(eventId);
        return activityRepository.findAllByEventIdAndSubeventNull(eventId);
    }

    public List<ActivitySiteDto> findAllForSite(UUID eventId) {
        return activityRepository.findAllForSiteByEventId(eventId);
    }

    public List<ActivitySiteDto> findAllForSite(UUID eventId, UUID subEventId) {
        return activityRepository.findAllForSiteByEventIdAndSubeventId(eventId, subEventId);
    }

    public List<Activity> findAll(UUID eventId, UUID subeventId) {
        checksEventExists(eventId);
        checksSubeventExists(subeventId);
        checkIfEventIsAssociateToSubevent(eventId, getSubEvent(subeventId));
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
        checkIfEventIsAssociateToSubevent(eventId, getSubEvent(subeventId));
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
        auditService.logAdminDelete(ResourceName.ACTIVITY, activityId);
    }

    public void delete(UUID eventId, UUID subeventId, UUID activityId) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubEvent(subeventId);
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
        auditService.logAdmin(Action.DELETE, ResourceName.ACTIVITY, activityId);
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

        if(activitySpeakerRepository.existsBySpeakerIdAndActivityId(dto.getSpeakerId(), activityId)) {
            throw new BusinessRuleException(BusinessRuleType.SPEAKER_ADD_ALREADY_EXISTS);
        }

        Speaker speaker = getSpeaker(dto.getSpeakerId());

        ActivitySpeaker activitySpeaker = new ActivitySpeaker(activity, speaker);
        activitySpeakerRepository.save(activitySpeaker);

        auditService.logAdmin(Action.CREATE, ResourceName.ACTIVITY_SPEAKER, activitySpeaker.getId());
        auditService.logAdminUpdate(ResourceName.ACTIVITY, String.format("Ministrante de email %s adicionado", speaker.getEmail()), activityId);
        auditService.logAdminUpdate(ResourceName.SPEAKER, String.format("Ministrante da atividade %s", activity.getTitle()), speaker.getId());

        return activitySpeaker;
    }

    public ActivitySpeaker addActivitySubEventSpeaker(UUID eventId, UUID subeventId, UUID activityId, ActivitySpeakerCreateDto dto) {
        Event event = getEvent(eventId);
        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SPEAKER_ADD_WITH_EVENT_CANCELED_STATUS);
        }

        Subevent subevent = getSubEvent(subeventId);
        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SPEAKER_ADD_WITH_SUBEVENT_CANCELED_STATUS);
        }

        Activity activity = getActivity(activityId);
        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SPEAKER_ADD_WITH_ACTIVITY_CANCELED_STATUS);
        }

        if(activitySpeakerRepository.existsBySpeakerIdAndActivityId(dto.getSpeakerId(), activityId)) {
            throw new BusinessRuleException(BusinessRuleType.SPEAKER_ADD_ALREADY_EXISTS);
        }

        Speaker speaker = getSpeaker(dto.getSpeakerId());

        ActivitySpeaker activitySpeaker = new ActivitySpeaker(activity, speaker);
        activitySpeakerRepository.save(activitySpeaker);

        auditService.logAdmin(Action.CREATE, ResourceName.ACTIVITY_SPEAKER, activitySpeaker.getId());
        auditService.logAdminUpdate(ResourceName.ACTIVITY, String.format("Ministrante de email %s adicionado", speaker.getEmail()), activityId);
        auditService.logAdminUpdate(ResourceName.SPEAKER, String.format("Ministrante da atividade %s", activity.getTitle()), speaker.getId());

        return activitySpeaker;
    }

    public void deleteActivityEventSpeaker(UUID eventId, UUID activityId, UUID activitySpeakerId) {
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        activitySpeakerRepository.deleteById(activitySpeakerId);
        log.info("Activity Speaker deleted: id={}, title={}", activityId, activity.getTitle());
        auditService.logAdminUpdate(ResourceName.ACTIVITY, String.format("Activity Speaker of id %s removed", activitySpeakerId), activityId);
    }

    public void deleteActivitySubEventSpeaker(UUID eventId, UUID subeventId, UUID activityId, UUID activitySpeakerId) {
        Activity activity = getActivity(activityId);
        checkIfEventIsAssociateToSubevent(eventId, activity.getSubevent());
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        activitySpeakerRepository.deleteById(activitySpeakerId);
        log.info("Activity Speaker deleted: id={}, title={}", activityId, activity.getTitle());
        auditService.logAdminUpdate(ResourceName.ACTIVITY, String.format("Activity Speaker of id %s removed", activitySpeakerId), activityId);
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

    private Subevent getSubEvent(UUID subeventId) {
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
    public void cancelAllByEventId(UUID eventId, String reason) {

        List<Activity> activities = new ArrayList<>();
        for (Activity activity : this.findAll(eventId)) {

            if(activity.getStatus().equals(EventStatus.PUBLISHED) &&
                (activity.getEvent().getRegistrationPeriod().getStartDate().isBefore(LocalDate.now()) ||
                    activity.getEvent().getRegistrationPeriod().getStartDate().isEqual(LocalDate.now()))
            ) {
                activity.setStatus(EventStatus.CANCELED);
                activities.add(activity);
            }

            sessionService.cancelAllByActivityId(eventId, activity.getId(), reason);

        }

        activityRepository.saveAll(activities);
    }

    @Transactional
    public void cancelAllBySubeventId(UUID eventId, UUID subeventId, String reason) {

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

            sessionService.cancelAllByActivityId(eventId, subeventId, activity.getId(), reason);
        }

        activityRepository.saveAll(activities);
    }
}
