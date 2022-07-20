package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public Event create(EventCreateDto dto) {
        if(eventRepository.existsByTitle(dto.getTitle())) {
            throw new ResourceAlreadyExistsException("event", "title", dto.getTitle());
        }

        if(eventRepository.existsBySlug(dto.getSlug())) {
            throw new ResourceAlreadyExistsException("event", "slug", dto.getSlug());
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
}
