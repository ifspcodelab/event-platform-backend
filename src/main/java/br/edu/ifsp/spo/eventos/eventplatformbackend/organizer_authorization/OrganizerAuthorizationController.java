package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventMapper;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer.OrganizerService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent.OrganizerSubeventService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("events")
    public ResponseEntity<List<EventDto>> eventsIndex (Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        UUID accountId = jwtUserDetails.getId();
        List<Event> events = organizerService.findAllEvents(accountId);

        return ResponseEntity.ok(eventMapper.to(events));
    }

    @GetMapping("subevents")
    public ResponseEntity<List<SubeventDto>> subeventsIndex (Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        UUID accountId = jwtUserDetails.getId();
        List<Subevent> subevents = organizerSubeventService.findAllSubevents(accountId);

        return ResponseEntity.ok(subeventMapper.to(subevents));
    }

    //TODO: get sessions
//    @GetMapping("events/sessions") {}
//
//    @GetMapping("subevents/sessions") {}
}
