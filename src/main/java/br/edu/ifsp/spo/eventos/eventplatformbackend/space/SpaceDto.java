package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SpaceDto {
    UUID id;
    String name;
    Integer capacity;
    SpaceType type;
}
