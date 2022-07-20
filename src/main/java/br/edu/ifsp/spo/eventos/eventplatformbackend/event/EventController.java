package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

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

        return new ResponseEntity<>(eventDto, HttpStatus.CREATED);
    }
}
