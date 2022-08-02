package br.edu.ifsp.spo.eventos.eventplatformbackend.speaker;

import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
}
