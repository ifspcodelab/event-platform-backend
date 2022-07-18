package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/{eventId}/sub-events")
public class SubeventController {

    @PostMapping
    public ResponseEntity<SubeventDto> create(@PathVariable UUID eventId, @Valid @RequestBody SubeventCreateDto subeventCreateDto) {
        System.out.println(eventId);
        SubeventDto subeventDto = new SubeventDto(
                UUID.randomUUID(),
                subeventCreateDto.getTitle(),
                subeventCreateDto.getSlug(),
                subeventCreateDto.getSummary(),
                subeventCreateDto.getPresentation(),
                subeventCreateDto.getStartDate(),
                subeventCreateDto.getEndDate(),
                subeventCreateDto.getSmallerImage(),
                subeventCreateDto.getBiggerImage(),
                EventStatus.DRAFT
        );
        return new ResponseEntity<>(subeventDto, HttpStatus.CREATED);
    }

}
