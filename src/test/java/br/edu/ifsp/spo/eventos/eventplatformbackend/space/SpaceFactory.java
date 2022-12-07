package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaFactory;

public class SpaceFactory {
    public static Space sampleSpace() {
        return new Space(
                "IVO",
                100,
                SpaceType.AUDITORIUM,
                AreaFactory.sampleArea()
        );
    }
}
