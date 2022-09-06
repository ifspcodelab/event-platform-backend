package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/organizers/authorization")
@AllArgsConstructor
public class OrganizerAuthorizationController {
    private final OrganizerAuthorizationService organizerAuthorizationService;
}
