package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/events")
public class EventController {
    @PostMapping
    public ResponseEntity<EventDto> create(@RequestBody EventCreateDto eventCreateDto) {
        EventDto eventDto = new EventDto(
                UUID.randomUUID(),
                eventCreateDto.getTitle(),
                eventCreateDto.getSlug(),
                eventCreateDto.getSummary(),
                eventCreateDto.getPresentation(),
                eventCreateDto.getRegistrationStartDate(),
                eventCreateDto.getRegistrationEndDate(),
                eventCreateDto.getStartDate(),
                eventCreateDto.getEndDate(),
                eventCreateDto.getSmallerImage(),
                eventCreateDto.getBiggerImage(),
                EventStatus.DRAFT
        );
        return new ResponseEntity<>(eventDto, HttpStatus.CREATED);
    }
}
