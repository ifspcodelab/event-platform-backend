package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationFactory;

public class AreaFactory {
    public static Area sampleArea() {
        return new Area(
                "Bloco A",
                "Piso Superior",
                LocationFactory.sampleLocation()
        );
    }
}
