package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.dto.CancellationMessageCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/events/{eventId}/sub-events")
@AllArgsConstructor
public class SubeventController {
    private final SubeventService subeventService;
    private final SubeventMapper subeventMapper;

    @PostMapping
    public ResponseEntity<SubeventDto> create(@PathVariable UUID eventId, @Valid @RequestBody SubeventCreateDto subeventCreateDto) {
        Subevent subevent = subeventService.create(subeventCreateDto, eventId);

        SubeventDto subeventDto = subeventMapper.to(subevent);

        return new ResponseEntity<>(subeventDto, HttpStatus.CREATED);
    }

    @GetMapping("{subeventId}")
    public ResponseEntity<SubeventDto> show(@PathVariable UUID eventId, @PathVariable UUID subeventId) {
        Subevent subevent = subeventService.findById(eventId, subeventId);

        SubeventDto subeventDto = subeventMapper.to(subevent);

        return ResponseEntity.ok(subeventDto);
    }

    @GetMapping
    public ResponseEntity<List<SubeventDto>> index(@PathVariable UUID eventId, @RequestParam(required = false) String slug) {
        if(slug != null) {
            List<Subevent> subevents =  List.of(subeventService.findBySlug(eventId, slug));
            return ResponseEntity.ok(subeventMapper.to(subevents));
        }

        List<Subevent> subevents = subeventService.findAll(eventId);

        return ResponseEntity.ok(subeventMapper.to(subevents));
    }

    @DeleteMapping("{subeventId}")
    public ResponseEntity<Void> delete(@PathVariable UUID eventId, @PathVariable UUID subeventId, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        subeventService.delete(eventId, subeventId, jwtUserDetails.getId());

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{subeventId}")
    public ResponseEntity<SubeventDto> update(@PathVariable UUID eventId, @PathVariable UUID subeventId, @Valid @RequestBody SubeventCreateDto subeventCreateDto) {
        Subevent subevent = subeventService.update(eventId, subeventId, subeventCreateDto);

       return ResponseEntity.ok(subeventMapper.to(subevent));
    }

    @PatchMapping("{subeventId}/cancel")
    public ResponseEntity<SubeventDto> cancel(@PathVariable UUID eventId, @PathVariable UUID subeventId, @Valid @RequestBody CancellationMessageCreateDto cancellationMessageCreateDto, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        Subevent subevent = subeventService.cancel(eventId, subeventId, cancellationMessageCreateDto, jwtUserDetails.getId());

        return ResponseEntity.ok(subeventMapper.to(subevent));
    }

    @PatchMapping("{subeventId}/publish")
    public ResponseEntity<SubeventDto> publish(@PathVariable UUID eventId, @PathVariable UUID subeventId, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        Subevent subevent = subeventService.publish(eventId, subeventId, jwtUserDetails.getId());

        return ResponseEntity.ok(subeventMapper.to(subevent));
    }

    @PatchMapping("{subeventId}/unpublish")
    public ResponseEntity<SubeventDto> unpublish(@PathVariable UUID eventId, @PathVariable UUID subeventId, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        Subevent subevent = subeventService.unpublish(eventId, subeventId, jwtUserDetails.getId());

        return ResponseEntity.ok(subeventMapper.to(subevent));
    }

}
