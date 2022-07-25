package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/{eventId}/sub-events")
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
    public ResponseEntity<List<SubeventDto>> index(@PathVariable UUID eventId) {
        List<Subevent> subevents = subeventService.findAll(eventId);

        return ResponseEntity.ok(subeventMapper.to(subevents));
    }

    @DeleteMapping("{subeventId}")
    public ResponseEntity<Void> delete(@PathVariable UUID eventId, @PathVariable UUID subeventId) {
        subeventService.delete(eventId, subeventId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{subeventId}")
    public ResponseEntity<SubeventDto> update(@PathVariable UUID eventId, @PathVariable UUID subeventId, @Valid @RequestBody SubeventCreateDto subeventCreateDto) {
        Subevent subevent = subeventService.update(eventId, subeventId, subeventCreateDto);

       return ResponseEntity.ok(subeventMapper.to(subevent));
    }

    @PatchMapping("{subeventId}/cancel")
    public ResponseEntity<SubeventDto> cancel(@PathVariable UUID eventId, @PathVariable UUID subeventId) {
        Subevent subevent = subeventService.cancel(eventId, subeventId);

        return ResponseEntity.ok(subeventMapper.to(subevent));
    }

    @PatchMapping("{subeventId}/publish")
    public ResponseEntity<SubeventDto> publish(@PathVariable UUID eventId, @PathVariable UUID subeventId) {
        Subevent subevent = subeventService.publish(eventId, subeventId);

        return ResponseEntity.ok(subeventMapper.to(subevent));
    }

    @PatchMapping("{subeventId}/unpublish")
    public ResponseEntity<SubeventDto> unpublish(@PathVariable UUID eventId, @PathVariable UUID subeventId) {
        Subevent subevent = subeventService.unpublish(eventId, subeventId);

        return ResponseEntity.ok(subeventMapper.to(subevent));
    }

}
