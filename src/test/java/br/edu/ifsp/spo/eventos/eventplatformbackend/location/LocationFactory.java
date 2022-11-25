package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import java.util.UUID;

public class LocationFactory {
    public static Location sampleLocation() {
        return new Location(
                UUID.randomUUID(),
                "IFSP Campus São Paulo",
                "R. Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
        );
    }

    public static Location sampleLocationHardcodedUuid() {
        return new Location(
                UUID.fromString("73c8b552-1d2c-4d62-9506-90697b53aa85"),
                "IFSP Campus São Paulo",
                "R. Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
        );
    }
}
