package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
@Slf4j
public class RegistrationController {
    private final RegistrationService registrationService;
    private final RegistrationMapper registrationMapper;

    @PostMapping("events/{eventId}/activities/{activityId}/sessions/{sessionId}/registrations")
    public ResponseEntity<RegistrationDto> create(@PathVariable UUID eventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @RequestBody RegistrationCreateDto registrationCreateDto) {
        var registration = registrationService.create(registrationCreateDto, eventId, activityId, sessionId);

        return new ResponseEntity<>(registrationMapper.to(registration), HttpStatus.CREATED);
    }

    @PostMapping("events/{eventId}/sub-events/{subeventId}/activities/{activityId}/sessions/{sessionId}/registrations")
    public ResponseEntity<RegistrationDto> create(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @RequestBody RegistrationCreateDto registrationCreateDto) {
        var registration = registrationService.create(registrationCreateDto, eventId, subeventId, activityId, sessionId);

        return new ResponseEntity<>(registrationMapper.to(registration), HttpStatus.CREATED);
    }

    @PostMapping("sessions/{sessionId}/registrations")
    public ResponseEntity<RegistrationDto> create(@PathVariable UUID sessionId, @RequestBody RegistrationCreateDto registrationCreateDto, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();

        if(!jwtUserDetails.getId().equals(registrationCreateDto.getAccountId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        var registration = registrationService.create(registrationCreateDto, sessionId);

        return new ResponseEntity<>(registrationMapper.to(registration), HttpStatus.CREATED);
    }

    @PostMapping("events/{eventId}/activities/{activityId}/sessions/{sessionId}/registrations/wait-list")
    public ResponseEntity<RegistrationDto> createRegistrationInWaitList(@PathVariable UUID eventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @RequestBody RegistrationCreateDto registrationCreateDto) {
        var registration = registrationService.createRegistrationInWaitList(registrationCreateDto, eventId, activityId, sessionId);

        return new ResponseEntity<>(registrationMapper.to(registration), HttpStatus.CREATED);
    }

    @PostMapping("events/{eventId}/sub-events/{subeventId}/activities/{activityId}/sessions/{sessionId}/registrations/wait-list")
    public ResponseEntity<RegistrationDto> createRegistrationInWaitList(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @RequestBody RegistrationCreateDto registrationCreateDto) {
        var registration = registrationService.createRegistrationInWaitList(registrationCreateDto, eventId, subeventId, activityId, sessionId);

        return new ResponseEntity<>(registrationMapper.to(registration), HttpStatus.CREATED);
    }

    @PostMapping("sessions/{sessionId}/registrations/wait-list")
    public ResponseEntity<RegistrationDto> createRegistrationInWaitList(@PathVariable UUID sessionId, @RequestBody RegistrationCreateDto registrationCreateDto, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();

        if(!jwtUserDetails.getId().equals(registrationCreateDto.getAccountId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        var registration = registrationService.createRegistrationInWaitList(registrationCreateDto, sessionId);

        return new ResponseEntity<>(registrationMapper.to(registration), HttpStatus.CREATED);
    }

    @GetMapping("events/{eventId}/activities/{activityId}/sessions/{sessionId}/registrations")
    public ResponseEntity<List<RegistrationDto>> index(@PathVariable UUID eventId, @PathVariable UUID activityId, @PathVariable UUID sessionId) {
        List<Registration> registrations = registrationService.findAll(eventId, activityId, sessionId);

        return ResponseEntity.ok(registrationMapper.to(registrations));
    }

    @GetMapping("events/{eventId}/sub-events/{subeventId}/activities/{activityId}/sessions/{sessionId}/registrations")
    public ResponseEntity<List<RegistrationDto>> index(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @PathVariable UUID sessionId) {
        List<Registration> registrations = registrationService.findAll(eventId, subeventId, activityId, sessionId);

        return ResponseEntity.ok(registrationMapper.to(registrations));
    }

    @GetMapping("accounts/events/{eventId}/registrations")
    public ResponseEntity<List<RegistrationDto>> index(@PathVariable UUID eventId, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        List<Registration> registrations = registrationService.findAll(eventId, jwtUserDetails.getId());

        return ResponseEntity.ok(registrationMapper.to(registrations));
    }

    @PatchMapping("events/{eventId}/activities/{activityId}/sessions/{sessionId}/registrations/{registrationId}/cancel")
    public ResponseEntity<RegistrationDto> cancel(@PathVariable UUID eventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @PathVariable UUID registrationId) {
        Registration registration = registrationService.cancel(eventId, activityId, sessionId, registrationId);
        return ResponseEntity.ok(registrationMapper.to(registration));
    }

    @PatchMapping("events/{eventId}/sub-events/{subeventId}/activities/{activityId}/sessions/{sessionId}/registrations/{registrationId}/cancel")
    public ResponseEntity<RegistrationDto> cancel(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @PathVariable UUID registrationId) {
        Registration registration = registrationService.cancel(eventId, subeventId, activityId, sessionId, registrationId);
        return ResponseEntity.ok(registrationMapper.to(registration));
    }

    @PatchMapping("accounts/events/{eventId}/registrations/{registrationId}/cancel")
    public ResponseEntity<RegistrationDto> cancel(@PathVariable UUID eventId, @PathVariable UUID registrationId, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();

        Registration registration = registrationService.cancel(jwtUserDetails.getId(), eventId, registrationId);
        return ResponseEntity.ok(registrationMapper.to(registration));
    }

    @PatchMapping("accounts/events/{eventId}/registrations/{registrationId}/accept")
    public ResponseEntity<RegistrationDto> acceptSessionSeat(@PathVariable UUID eventId, @PathVariable UUID registrationId, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();

        Registration registration = registrationService.acceptSessionSeat(jwtUserDetails.getId(), eventId, registrationId);
        return ResponseEntity.ok(registrationMapper.to(registration));
    }

    @PatchMapping("accounts/events/{eventId}/registrations/{registrationId}/deny")
    public ResponseEntity<RegistrationDto> denySessionSeat(@PathVariable UUID eventId, @PathVariable UUID registrationId, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();

        Registration registration = registrationService.denySessionSeat(jwtUserDetails.getId(), eventId, registrationId);
        return ResponseEntity.ok(registrationMapper.to(registration));
    }
}
