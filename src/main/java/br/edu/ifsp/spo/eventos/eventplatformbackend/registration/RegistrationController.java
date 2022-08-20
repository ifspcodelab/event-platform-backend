package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;
    private final RegistrationMapper registrationMapper;

    @PostMapping("events/{eventId}/activities/{activityId}/sessions/{sessionId}/registrations")
    public ResponseEntity<RegistrationDto> create(@PathVariable UUID eventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @RequestBody(required = false) RegistrationCreateDto registrationCreateDto) {
        if(registrationCreateDto != null) {
            var registration = registrationService.create(registrationCreateDto.getAccountId(), eventId, activityId, sessionId);

            return new ResponseEntity<>(registrationMapper.to(registration), HttpStatus.CREATED);
        }

        UUID accountId = UUID.fromString("4162a226-9187-460f-a54b-a378c580e2a5");

        var registration = registrationService.create(accountId, eventId, activityId, sessionId);

        return new ResponseEntity<>(registrationMapper.to(registration), HttpStatus.CREATED);
    }

    @PostMapping("events/{eventId}/sub-events/{subeventId}/activities/{activityId}/sessions/{sessionId}/registrations")
    public ResponseEntity<RegistrationDto> create(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @RequestBody(required = false) RegistrationCreateDto registrationCreateDto) {
        if(registrationCreateDto != null) {
            var registration = registrationService.create(registrationCreateDto.getAccountId(), eventId, activityId, sessionId);

            return new ResponseEntity<>(registrationMapper.to(registration), HttpStatus.CREATED);
        }

        UUID accountId = UUID.fromString("4162a226-9187-460f-a54b-a378c580e2a5");

        var registration = registrationService.create(accountId, eventId, subeventId, activityId, sessionId);

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

    @PatchMapping("events/{eventId}/activities/{activityId}/sessions/{sessionId}/registrations/{registrationId}")
    public ResponseEntity<RegistrationDto> cancel(@PathVariable UUID eventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @PathVariable UUID registrationId) {
        UUID accountId = UUID.fromString("4162a226-9187-460f-a54b-a378c580e2a5");

        Registration registration = registrationService.cancel(accountId, eventId, activityId, sessionId, registrationId);
        return ResponseEntity.ok(registrationMapper.to(registration));
    }

    @PatchMapping("events/{eventId}/sub-events/{subeventId}/activities/{activityId}/sessions/{sessionId}/registrations/{registrationId}")
    public ResponseEntity<RegistrationDto> cancel(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @PathVariable UUID registrationId) {
        UUID accountId = UUID.fromString("4162a226-9187-460f-a54b-a378c580e2a5");

        Registration registration = registrationService.cancel(accountId, eventId, subeventId, activityId, sessionId, registrationId);
        return ResponseEntity.ok(registrationMapper.to(registration));
    }
}
