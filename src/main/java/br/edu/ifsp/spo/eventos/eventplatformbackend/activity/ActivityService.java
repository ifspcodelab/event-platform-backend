package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
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

    public Activity create(UUID eventId, ActivityCreateDto dto) {
        Event event = getEvent(eventId);

        if(activityRepository.existsByTitleIgnoreCaseAndEventId(dto.getTitle(), eventId)) {
            throw new ResourceAlreadyExistsException(ResourceName.ACTIVITY, "title", dto.getTitle());
        }

        if(event.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_CREATE_WITH_EVENT_CANCELED_STATUS);
        }

        if(!event.getRegistrationPeriod().getEndDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

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

    public Activity update(UUID eventId, UUID activityId, ActivityCreateDto dto) {
//        Event event = getEvent(eventId);
        Activity activity = getActivity(activityId);

        activity.setTitle(dto.getTitle());
        activity.setSlug(dto.getSlug());
        activity.setDescription(dto.getDescription());
        activity.setType(dto.getActivityType());
        activity.setOnline(dto.isOnline());
        activity.setNeedRegistration(dto.isNeedRegistration());

        return activityRepository.save(activity);
    }

    public Activity publish(UUID eventId, UUID activityId) {
        Activity activity = getActivity(activityId);
        activity.setStatus(EventStatus.PUBLISHED);
        return activityRepository.save(activity);
    }

    public Activity unpublish(UUID eventId, UUID activityId) {
        Activity activity = getActivity(activityId);
        activity.setStatus(EventStatus.DRAFT);
        return activityRepository.save(activity);
    }

    public List<Activity> findALl(UUID eventId) {
        return activityRepository.findAllByEventId(eventId);
    }

    public Activity findById(UUID eventId, UUID activityId) {
        Activity activity = getActivity(activityId);
        //TODO - verificar se atividade existe associada ao evento
        return activity;
    }

    public void delete(UUID eventId, UUID activityId) {
        Activity activity = getActivity(activityId);
        activityRepository.delete(activity);
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
    }

    private Activity getActivity(UUID activityId) {
        return activityRepository.findById(activityId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACTIVITY, activityId));
    }
}
