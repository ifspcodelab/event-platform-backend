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
    public ResponseEntity<List<EventDto>> index(@RequestParam(required = false) String slug) {
        if(slug != null) {
            List<EventDto> events = List.of(eventService.findBySlug(slug));
        }
        List<Event> events = eventService.findAll();

        return ResponseEntity.ok(eventMapper.to(events));
    }

    @DeleteMapping("{eventId}")
    public ResponseEntity<Void> delete(@PathVariable UUID eventId) {
        eventService.delete(eventId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{eventId}")
    public ResponseEntity<EventDto> update(@PathVariable UUID eventId, @Valid @RequestBody EventCreateDto eventCreateDto) {
        Event event = eventService.update(eventId, eventCreateDto);

        return ResponseEntity.ok(eventMapper.to(event));
    }

    @PatchMapping("{eventId}/cancel")
    public ResponseEntity<EventDto> cancel(@PathVariable UUID eventId, @Valid @RequestBody CancellationMessageCreateDto cancellationMessage) {
        Event event = eventService.cancel(eventId, cancellationMessage);

        return ResponseEntity.ok(eventMapper.to(event));
    }

    @PatchMapping("{eventId}/publish")
    public ResponseEntity<EventDto> publish(@PathVariable UUID eventId) {
        Event event = eventService.publish(eventId);

        return ResponseEntity.ok(eventMapper.to(event));
    }

    @PatchMapping("{eventId}/unpublish")
    public ResponseEntity<EventDto> unpublish(@PathVariable UUID eventId) {
        Event event = eventService.unpublish(eventId);

        return ResponseEntity.ok(eventMapper.to(event));
    }
}
