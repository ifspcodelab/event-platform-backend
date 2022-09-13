package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventMapper;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer.OrganizerService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent.OrganizerSubeventService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionMapper;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/organizers")
@AllArgsConstructor
public class OrganizerAuthorizationController {
    private final OrganizerService organizerService;
    private final OrganizerSubeventService organizerSubeventService;
    private final EventMapper eventMapper;
    private final SubeventMapper subeventMapper;
    private final SessionMapper sessionMapper;

    @GetMapping("events")
    public ResponseEntity<List<EventDto>> eventsIndex (Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        UUID accountId = jwtUserDetails.getId();
        List<Event> events = organizerService.findAllEvents(accountId);

        return ResponseEntity.ok(eventMapper.to(events));
    }

    @GetMapping("sub-events")
    public ResponseEntity<List<SubeventDto>> subeventsIndex (Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        UUID accountId = jwtUserDetails.getId();
        List<Subevent> subevents = organizerSubeventService.findAllSubevents(accountId);

        return ResponseEntity.ok(subeventMapper.to(subevents));
    }

    @GetMapping("events/{eventId}/sessions")
    public ResponseEntity<List<SessionDto>> eventSessionsIndex (@PathVariable UUID eventId, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        UUID accountId = jwtUserDetails.getId();
        List<Session> sessions = organizerService.findAllSessions(eventId, accountId);

        return ResponseEntity.ok(sessionMapper.to(sessions));
    }

    @GetMapping("sub-events/{subeventId}/sessions")
    public ResponseEntity<List<SessionDto>> subeventsSessionsIndex (@PathVariable UUID subeventId, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        UUID accountId = jwtUserDetails.getId();
        List<Session> sessions = organizerSubeventService.findAllSessions(subeventId, accountId);

        return ResponseEntity.ok(sessionMapper.to(sessions));
    }

    @GetMapping("events/{eventId}/sessions/{sessionId}")
    public ResponseEntity<SessionDto> showEventSession (@PathVariable UUID eventId, @PathVariable UUID sessionId) {
        Session session = organizerService.findSessionById(eventId, sessionId);

        return ResponseEntity.ok(sessionMapper.to(session));
    }

    @GetMapping("sub-events/{subeventId}/sessions/{sessionId}")
    public ResponseEntity<SessionDto> showSubeventSession (@PathVariable UUID subeventId, @PathVariable UUID sessionId) {
        Session session = organizerSubeventService.findSessionById(subeventId, sessionId);

        return ResponseEntity.ok(sessionMapper.to(session));
    }
}
