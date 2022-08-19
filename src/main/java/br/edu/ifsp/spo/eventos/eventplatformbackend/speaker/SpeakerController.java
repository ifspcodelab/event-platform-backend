package br.edu.ifsp.spo.eventos.eventplatformbackend.speaker;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
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
    public ResponseEntity<SpeakerDto> update(@PathVariable UUID speakerId, @RequestBody @Valid SpeakerCreateDto dto, Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        System.out.println(jwtUserDetails);
        Speaker speaker = speakerService.update(speakerId, dto);
        SpeakerDto speakerDto = speakerMapper.to(speaker);
        return ResponseEntity.ok(speakerDto);
    }

    @GetMapping()
    public ResponseEntity<Page<SpeakerDto>> index(Pageable pageable) {
        Page<Speaker> speakers = speakerService.findAll(pageable);
        return ResponseEntity.ok(speakers.map(speakerMapper::to));
    }

    @GetMapping("{speakerId}")
    public ResponseEntity<SpeakerDto> show(@PathVariable UUID speakerId) {
        Speaker speaker = speakerService.findById(speakerId);
        SpeakerDto speakerDto = speakerMapper.to(speaker);
        return ResponseEntity.ok(speakerDto);
    }

    @DeleteMapping("{speakerId}")
    public ResponseEntity<Void> delete(@PathVariable UUID speakerId) {
        speakerService.delete(speakerId);
        return ResponseEntity.noContent().build();
    }
}
