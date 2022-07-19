package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SubeventService {
    private final SubeventRepository subeventRepository;
    private final EventRepository eventRepository;

    public Subevent create(SubeventCreateDto dto, UUID eventId) {
        Event event = getEvent(eventId);

        if(subeventRepository.existsByTitleAndEvent(dto.getTitle(), event)) {
            throw new ResourceAlreadyExistsException("subevent","title", dto.getTitle());
        }

        if(subeventRepository.existsBySlugAndEvent(dto.getSlug(), event)) {
            throw new ResourceAlreadyExistsException("subevent","slug", dto.getSlug());
        }

        Subevent subevent = new Subevent(
                dto.getTitle(),
                dto.getSlug(),
                dto.getSummary(),
                dto.getPresentation(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getSmallerImage(),
                dto.getBiggerImage(),
                event
        );
        return subeventRepository.save(subevent);
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("event", eventId));
    }
}
