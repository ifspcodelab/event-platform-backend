package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationFactory;

import java.util.UUID;

public class AreaFactory {
    public static Area sampleArea() {
        return new Area(
                "Bloco A",
                "Piso Superior",
                LocationFactory.sampleLocation()
        );
    }

    public static Area sampleAreaWithHardcodedLocationUuid() {
        return new Area(
                UUID.fromString("e023fcfb-5a6a-4f8b-9c2d-ff768f3eb6e0"),
                "Bloco A",
                "Piso Superior",
                LocationFactory.sampleLocationWithHardcodedUuid()
        );
    }
}