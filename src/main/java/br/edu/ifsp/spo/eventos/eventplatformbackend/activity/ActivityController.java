package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

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
public class ActivityController {
    private final ActivityService activityService;
    private final ActivityMapper activityMapper;

    @PostMapping("{activities}")
    public ResponseEntity<ActivityDto> create (@PathVariable UUID eventId, @Valid @RequestBody ActivityCreateDto activityCreateDto) {
        Activity activity = activityService.create(eventId, activityCreateDto);
        ActivityDto activityDto = activityMapper.to(activity);
        return new ResponseEntity<>(activityDto, HttpStatus.CREATED);
    }

    @GetMapping("{activities}")
    public ResponseEntity<List<ActivityDto>> index (@PathVariable UUID eventId) {
        List<Activity> activities = activityService.findALl(eventId);
        return ResponseEntity.ok(activityMapper.to(activities));
    }
}