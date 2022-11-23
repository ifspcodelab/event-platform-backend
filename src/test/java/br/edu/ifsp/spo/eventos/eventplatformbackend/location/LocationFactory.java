package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import java.util.UUID;

public class LocationFactory {
    public static Location validLocation() {
        return new Location(
                UUID.randomUUID(),
                "IFSP Campus São Paulo",
                "R. Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
        );
    }
}
