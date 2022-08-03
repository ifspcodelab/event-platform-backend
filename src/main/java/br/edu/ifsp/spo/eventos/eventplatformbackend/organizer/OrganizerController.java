package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events/{eventsId}")
public class OrganizerController {
    private OrganizerService organizerService;
    private OrganizerMapper organizerMapper;

    @PostMapping
    public ResponseEntity<OrganizerDto> create(@PathVariable UUID accountId, @PathVariable UUID eventId, @RequestBody @Valid OrganizerCreateDto dto) {
        Organizer organizer = organizerService.create(accountId, eventId, dto);
        OrganizerDto organizerDto = organizerMapper.to(organizer);
        return new ResponseEntity<>(organizerDto, HttpStatus.CREATED);
    }
}
