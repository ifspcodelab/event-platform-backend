package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.dto.CancellationMessageCreateDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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

    @PutMapping("activities/{activityId}/sessions/{sessionId}")
    public ResponseEntity<SessionDto> update(@PathVariable UUID eventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @Valid @RequestBody SessionCreateDto sessionCreateDto) {
        Session session = sessionService.update(eventId, activityId, sessionId, sessionCreateDto);
        SessionDto sessionDto = sessionMapper.to(session);
        return ResponseEntity.ok(sessionDto);
    }

    @PutMapping("sub-events/{subeventId}/activities/{activityId}/sessions/{sessionId}")
    public ResponseEntity<SessionDto> update(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @Valid @RequestBody SessionCreateDto sessionCreateDto) {
        Session session = sessionService.update(eventId, subeventId, activityId, sessionId, sessionCreateDto);
        SessionDto sessionDto = sessionMapper.to(session);
        return ResponseEntity.ok(sessionDto);
    }

    @PatchMapping("activities/{activityId}/sessions/{sessionId}/cancel")
    public ResponseEntity<SessionDto> cancel(@PathVariable UUID eventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @Valid @RequestBody CancellationMessageCreateDto cancellationMessageCreateDto) {
        Session session = sessionService.cancel(eventId, activityId, sessionId, cancellationMessageCreateDto);
        return ResponseEntity.ok(sessionMapper.to(session));
    }

    @PatchMapping("sub-events/{subeventId}/activities/{activityId}/sessions/{sessionId}/cancel")
    public ResponseEntity<SessionDto> cancel(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @Valid @RequestBody CancellationMessageCreateDto cancellationMessageCreateDto) {
        Session session = sessionService.cancel(eventId, subeventId, activityId, sessionId, cancellationMessageCreateDto);
        return ResponseEntity.ok(sessionMapper.to(session));
    }

    @DeleteMapping("activities/{activityId}/sessions/{sessionId}")
    public ResponseEntity<Void> delete(@PathVariable UUID eventId, @PathVariable UUID activityId, @PathVariable UUID sessionId) {
        sessionService.delete(eventId, activityId, sessionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("sub-events/{subeventId}/activities/{activityId}/sessions/{sessionId}")
    public ResponseEntity<Void> delete(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @PathVariable UUID sessionId) {
        sessionService.delete(eventId, subeventId, activityId, sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("activities/{activityId}/sessions")
    public ResponseEntity<List<SessionDto>> index(@PathVariable UUID eventId, @PathVariable UUID activityId) {
        List<Session> sessions = sessionService.findAll(eventId, activityId);
        return ResponseEntity.ok(sessionMapper.to(sessions));
    }

    @GetMapping("sub-events/{subeventId}/activities/{activityId}/sessions")
    public ResponseEntity<List<SessionDto>> index(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId) {
        List<Session> sessions = sessionService.findAll(eventId, subeventId , activityId);
        return ResponseEntity.ok(sessionMapper.to(sessions));
    }

    @GetMapping("activities/{activityId}/sessions/{sessionId}")
    public ResponseEntity<SessionDto> show(@PathVariable UUID eventId, @PathVariable UUID activityId, @PathVariable UUID sessionId) {
        Session session = sessionService.findById(eventId, activityId, sessionId);
        return ResponseEntity.ok(sessionMapper.to(session));
    }

    @GetMapping("sub-events/{subeventId}/activities/{activityId}/sessions/{sessionId}")
    public ResponseEntity<SessionDto> show(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @PathVariable UUID sessionId) {
        Session session = sessionService.findById(eventId, subeventId , activityId, sessionId);
        return ResponseEntity.ok(sessionMapper.to(session));
    }
}
