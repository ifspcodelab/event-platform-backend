package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Value
public class ActivitySpeakerCreateDto {
    @NotNull
    UUID speakerId;
}
