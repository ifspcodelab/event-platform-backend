package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LocationDto {
    UUID id;
    String name;
    String address;
}
