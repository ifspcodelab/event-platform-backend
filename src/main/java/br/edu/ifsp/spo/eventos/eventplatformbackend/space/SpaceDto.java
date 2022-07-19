package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SpaceDto {

    private UUID id;
    private String name;
    private Integer capacity;
    private SpaceType type;

}
