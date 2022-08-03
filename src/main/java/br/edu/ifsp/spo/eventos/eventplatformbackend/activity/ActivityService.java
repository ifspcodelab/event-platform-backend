package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final EventRepository eventRepository;

    public Activity create(UUID eventId, ActivityCreateDto dto) {
        Event event = getEvent(eventId);

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

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
    }

    public List<Activity> findALl(UUID eventId) {
        return activityRepository.findAllByEventId(eventId);
    }

    public Activity findById(UUID eventId, UUID activityId) {
        Activity activity = getActivity(activityId);
        //TODO - verificar se atividade existe associada ao evento
        return activity;
    }

    private Activity getActivity(UUID activityId) {
        return activityRepository.findById(activityId)
        .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACTIVITY, activityId));
    }
}
