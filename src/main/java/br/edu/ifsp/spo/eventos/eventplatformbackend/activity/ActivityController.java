package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.dto.CancellationMessageCreateDto;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events/{eventId}")
@AllArgsConstructor
public class ActivityController {
    private final ActivityService activityService;
    private final ActivityMapper activityMapper;
    private final ActivitySpeakerMapper activitySpeakerMapper;

    @PostMapping("activities")
    public ResponseEntity<ActivityDto> create (@PathVariable UUID eventId, @Valid @RequestBody ActivityCreateDto activityCreateDto) {
        Activity activity = activityService.create(eventId, activityCreateDto);
        ActivityDto activityDto = activityMapper.to(activity);
        return new ResponseEntity<>(activityDto, HttpStatus.CREATED);
    }

    @PostMapping("sub-events/{subeventId}/activities")
    public ResponseEntity<ActivityDto> create (@PathVariable UUID eventId, @PathVariable UUID subeventId, @Valid @RequestBody ActivityCreateDto activityCreateDto) {
        Activity activity = activityService.create(eventId, subeventId, activityCreateDto);
        ActivityDto activityDto = activityMapper.to(activity);
        return new ResponseEntity<>(activityDto, HttpStatus.CREATED);
    }

    @PutMapping("activities/{activityId}")
    public ResponseEntity<ActivityDto> update(@PathVariable UUID eventId, @PathVariable UUID activityId, @Valid @RequestBody ActivityCreateDto activityCreateDto) {
        Activity activity = activityService.update(eventId, activityId, activityCreateDto);
        ActivityDto activityDto = activityMapper.to(activity);
        return ResponseEntity.ok(activityDto);
    }

    @PutMapping("sub-events/{subeventId}/activities/{activityId}")
    public ResponseEntity<ActivityDto> update(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @Valid @RequestBody ActivityCreateDto activityCreateDto) {
        Activity activity = activityService.update(eventId, subeventId, activityId, activityCreateDto);
        ActivityDto activityDto = activityMapper.to(activity);
        return ResponseEntity.ok(activityDto);
    }

    @PatchMapping("activities/{activityId}/publish")
    public ResponseEntity<ActivityDto> publish(@PathVariable UUID eventId, @PathVariable UUID activityId) {
        Activity activity = activityService.publish(eventId, activityId);
        return ResponseEntity.ok(activityMapper.to(activity));
    }

    @PatchMapping("sub-events/{subeventId}/activities/{activityId}/publish")
    public ResponseEntity<ActivityDto> publish(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId) {
        Activity activity = activityService.publish(eventId, subeventId, activityId);
        return ResponseEntity.ok(activityMapper.to(activity));
    }

    @PatchMapping("activities/{activityId}/unpublish")
    public ResponseEntity<ActivityDto> unpublish(@PathVariable UUID eventId, @PathVariable UUID activityId) {
        Activity activity = activityService.unpublish(eventId, activityId);
        return ResponseEntity.ok(activityMapper.to(activity));
    }

    @PatchMapping("sub-events/{subeventId}/activities/{activityId}/unpublish")
    public ResponseEntity<ActivityDto> unpublish(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId) {
        Activity activity = activityService.unpublish(eventId, subeventId, activityId);
        return ResponseEntity.ok(activityMapper.to(activity));
    }

    @PatchMapping("activities/{activityId}/cancel")
    public ResponseEntity<ActivityDto> cancel(@PathVariable UUID eventId, @PathVariable UUID activityId, @Valid @RequestBody CancellationMessageCreateDto cancellationMessageCreateDto) {
        Activity activity = activityService.cancel(eventId, activityId, cancellationMessageCreateDto);
        return ResponseEntity.ok(activityMapper.to(activity));
    }

    @PatchMapping("sub-events/{subeventId}/activities/{activityId}/cancel")
    public ResponseEntity<ActivityDto> cancel(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId, @Valid @RequestBody CancellationMessageCreateDto cancellationMessageCreateDto) {
        Activity activity = activityService.cancel(eventId, subeventId, activityId, cancellationMessageCreateDto);
        return ResponseEntity.ok(activityMapper.to(activity));
    }

    @GetMapping("activities")
    public ResponseEntity<List<ActivityDto>> index(@PathVariable UUID eventId) {
        List<Activity> activities = activityService.findALl(eventId);
        return ResponseEntity.ok(activityMapper.to(activities));
    }

    @GetMapping("sub-events/{subeventId}/activities")
    public ResponseEntity<List<ActivityDto>> index(@PathVariable UUID eventId, @PathVariable UUID subeventId) {
        List<Activity> activities = activityService.findAll(eventId, subeventId);
        return ResponseEntity.ok(activityMapper.to(activities));
    }

    @GetMapping("activities/{activityId}")
    public ResponseEntity<ActivityDto> show(@PathVariable UUID eventId, @PathVariable UUID activityId) {
        Activity activity = activityService.findById(eventId, activityId);
        ActivityDto activityDto = activityMapper.to(activity);
        return ResponseEntity.ok(activityDto);
    }

    @GetMapping("sub-events/{subeventId}/activities/{activityId}")
    public ResponseEntity<ActivityDto> show(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId) {
        Activity activity = activityService.findById(eventId, subeventId, activityId);
        ActivityDto activityDto = activityMapper.to(activity);
        return ResponseEntity.ok(activityDto);
    }

    @DeleteMapping("activities/{activityId}")
    public ResponseEntity<Void> delete(@PathVariable UUID eventId, @PathVariable UUID activityId) {
        activityService.delete(eventId, activityId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("sub-events/{subeventId}/activities/{activityId}")
    public ResponseEntity<Void> delete(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable UUID activityId) {
        activityService.delete(eventId, subeventId, activityId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("activities/{activityId}/speakers")
    public ResponseEntity<ActivitySpeakerDto> addActivityEventSpeaker(
        @PathVariable UUID eventId,
        @PathVariable UUID activityId,
        @Valid @RequestBody ActivitySpeakerCreateDto dto
    ) {
        ActivitySpeaker activitySpeaker = activityService.addActivityEventSpeaker(eventId, activityId, dto);
        ActivitySpeakerDto activitySpeakerDto = activitySpeakerMapper.to(activitySpeaker);
        return ResponseEntity.ok(activitySpeakerDto);
    }

    @PostMapping("sub-events/{subeventId}/activities/{activityId}/speakers")
    public ResponseEntity<ActivitySpeakerDto> addActivitySubEventSpeaker(
        @PathVariable UUID eventId,
        @PathVariable UUID subeventId,
        @PathVariable UUID activityId,
        @Valid @RequestBody ActivitySpeakerCreateDto dto
    ) {
        ActivitySpeaker activitySpeaker = activityService.addActivitySubEventSpeaker(eventId, subeventId, activityId, dto);
        ActivitySpeakerDto activitySpeakerDto = activitySpeakerMapper.to(activitySpeaker);
        return ResponseEntity.ok(activitySpeakerDto);
    }

    @DeleteMapping("activities/{activityId}/speakers/{activitySpeakerId}")
    public ResponseEntity<Void> deleteActivityEventSpeaker(
        @PathVariable UUID eventId,
        @PathVariable UUID activityId,
        @PathVariable UUID activitySpeakerId
    ) {
        activityService.deleteActivityEventSpeaker(eventId, activityId, activitySpeakerId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("sub-events/{subeventId}/activities/{activityId}/speakers/{activitySpeakerId}")
    public ResponseEntity<Void> deleteActivitySubEventSpeaker(
        @PathVariable UUID eventId,
        @PathVariable UUID subeventId,
        @PathVariable UUID activityId,
        @PathVariable UUID activitySpeakerId
    ) {
        activityService.deleteActivitySubEventSpeaker(eventId, subeventId, activityId, activitySpeakerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("activities/{activityId}/speakers")
    public ResponseEntity<List<ActivitySpeakerDto>> findAllActivityEventSpeaker(
        @PathVariable UUID eventId,
        @PathVariable UUID activityId
    ) {
        List<ActivitySpeaker> activitySpeaker = activityService.findAllActivityEventSpeaker(eventId, activityId);
        return ResponseEntity.ok(activitySpeakerMapper.to(activitySpeaker));
    }

    @GetMapping("sub-events/{subeventId}/activities/{activityId}/speakers")
    public ResponseEntity<List<ActivitySpeakerDto>> findAllActivitySubEventSpeaker(
        @PathVariable UUID eventId,
        @PathVariable UUID subeventId,
        @PathVariable UUID activityId
    ) {
        List<ActivitySpeaker> activitySpeaker = activityService.findAllActivitySubEventSpeaker(eventId, subeventId, activityId);
        return ResponseEntity.ok(activitySpeakerMapper.to(activitySpeaker));
    }
}