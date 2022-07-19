package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/{eventId}/sub-events")
@AllArgsConstructor
public class SubeventController {
    private final SubeventService subeventService;
    private final SubeventMapper subeventMapper;

    @PostMapping
    public ResponseEntity<SubeventDto> create(@PathVariable UUID eventId, @Valid @RequestBody SubeventCreateDto subeventCreateDto) {
        System.out.println(eventId);
        Subevent subevent = subeventService.create(subeventCreateDto, eventId);
        SubeventDto subeventDto = subeventMapper.to(subevent);
        return new ResponseEntity<>(subeventDto, HttpStatus.CREATED);
    }

}