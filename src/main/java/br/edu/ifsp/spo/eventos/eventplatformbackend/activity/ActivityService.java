package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
}
