package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/events")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    public ResponseEntity<EventDto> create(@Valid @RequestBody EventCreateDto eventCreateDto) {
        Event event = eventService.create(eventCreateDto);

        EventDto eventDto = eventMapper.to(event);

        return ResponseEntity.ok(eventDto);
    }

    @GetMapping("{eventId}")
    public ResponseEntity<EventDto> show(@PathVariable UUID eventId) {
        Event event = eventService.findById(eventId);

        EventDto eventDto = eventMapper.to(event);

        return ResponseEntity.ok(eventDto);
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> index() {
        List<Event> events = eventService.findAll();

        return ResponseEntity.ok(eventMapper.to(events));
    }

    @DeleteMapping("{eventId}")
    public ResponseEntity<Void> delete(@PathVariable UUID eventId) {
        eventService.delete(eventId);

        return ResponseEntity.noContent().build();
    }
}
