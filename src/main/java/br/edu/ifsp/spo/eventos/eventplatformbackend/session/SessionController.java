package br.edu.ifsp.spo.eventos.eventplatformbackend.session;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events/{eventId}")
@AllArgsConstructor
public class SessionController {
    private final SessionService sessionService;
    private final SessionMapper sessionMapper;

    @PostMapping("activities/{activityId}/sessions")
    public ResponseEntity<SessionDto> create (@PathVariable UUID eventId, @PathVariable UUID activityId, @Valid @RequestBody SessionCreateDto sessionCreateDto) {
        Session session = sessionService.create(eventId, activityId, sessionCreateDto);
        SessionDto sessionDto = sessionMapper.to(session);
        return new ResponseEntity<>(sessionDto, HttpStatus.CREATED);
    }

    @PostMapping("sub-events/{subeventId}/activities/{activityId}/sessions")
    public ResponseEntity<SessionDto> create (@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @Valid @RequestBody SessionCreateDto sessionCreateDto) {
        Session session = sessionService.create(eventId, subeventId, activityId, sessionCreateDto);
        SessionDto sessionDto = sessionMapper.to(session);
        return new ResponseEntity<>(sessionDto, HttpStatus.CREATED);
    }


}
