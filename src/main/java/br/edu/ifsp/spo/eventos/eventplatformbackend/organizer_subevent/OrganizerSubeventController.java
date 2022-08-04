package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events/{eventId}/sub-events/{subeventId}/organizers")
public class OrganizerSubeventController {
    private final OrganizerSubeventService organizerSubeventService;
    private final OrganizerSubeventMapper organizerSubeventMapper;

    @PostMapping
    public ResponseEntity<OrganizerSubeventDto> create(@PathVariable UUID eventId, @PathVariable UUID subeventId, @RequestBody @Valid OrganizerSubeventCreateDto organizerSubeventCreateDto) {
        OrganizerSubevent organizerSubevent = organizerSubeventService.create(eventId, subeventId, organizerSubeventCreateDto);
        OrganizerSubeventDto organizerSubeventDto = organizerSubeventMapper.to(organizerSubevent);
        return new ResponseEntity<>(organizerSubeventDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrganizerSubeventDto>> index(@PathVariable UUID eventId, @PathVariable UUID subeventId) {
        List<OrganizerSubevent> organizerSubevents = organizerSubeventService.findAll(eventId, subeventId);
        return ResponseEntity.ok(organizerSubeventMapper.to(organizerSubevents));

    }
}
