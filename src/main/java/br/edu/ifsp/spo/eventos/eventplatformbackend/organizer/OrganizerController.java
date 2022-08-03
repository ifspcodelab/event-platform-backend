package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events/{eventId}/organizers")
public class OrganizerController {
    private OrganizerService organizerService;
    private OrganizerMapper organizerMapper;

    @PostMapping
    public ResponseEntity<OrganizerDto> create(@PathVariable UUID eventId, @RequestBody @Valid OrganizerCreateDto dto) {
        Organizer organizer = organizerService.create(eventId, dto);
        OrganizerDto organizerDto = organizerMapper.to(organizer);
        return new ResponseEntity<>(organizerDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrganizerDto>> index(@PathVariable UUID eventId) {
        List<Organizer> organizers = organizerService.findAll(eventId);
        List<OrganizerDto> organizersDto = organizerMapper.to(organizers);
        return new ResponseEntity<>(organizersDto, HttpStatus.OK);
    }

}
