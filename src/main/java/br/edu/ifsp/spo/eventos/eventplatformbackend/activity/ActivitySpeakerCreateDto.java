package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class ActivitySpeakerCreateDto {
    @NotNull
    UUID speakerId;
}
