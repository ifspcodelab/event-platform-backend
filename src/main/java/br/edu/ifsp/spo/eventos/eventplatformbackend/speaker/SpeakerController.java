package br.edu.ifsp.spo.eventos.eventplatformbackend.speaker;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/speakers")
@AllArgsConstructor
public class SpeakerController {
    private final SpeakerService speakerService;
    private final SpeakerMapper speakerMapper;

    @PostMapping
    public ResponseEntity<SpeakerDto> create(@RequestBody @Valid SpeakerCreateDto dto) {
        Speaker speaker = speakerService.create(dto);
        SpeakerDto speakerDto = speakerMapper.to(speaker);
        return new ResponseEntity<>(speakerDto, HttpStatus.CREATED);
    }

    @PutMapping("{speakerId}")
    public ResponseEntity<SpeakerDto> update(@PathVariable UUID speakerId, @RequestBody @Valid SpeakerCreateDto dto) {
        Speaker speaker = speakerService.update(speakerId, dto);
        SpeakerDto speakerDto = speakerMapper.to(speaker);
        return ResponseEntity.ok(speakerDto);
    }

    @GetMapping("{speakerId}")
    public ResponseEntity<SpeakerDto> show(@PathVariable UUID speakerId) {
        Speaker speaker = speakerService.findById(speakerId);
        SpeakerDto speakerDto = speakerMapper.to(speaker);
        return ResponseEntity.ok(speakerDto);
    }
}
