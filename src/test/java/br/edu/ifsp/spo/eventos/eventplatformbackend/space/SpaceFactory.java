package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaFactory;

import java.util.UUID;

public class SpaceFactory {
    public static Space sampleSpace() {
        return new Space(
                "IVO",
                100,
                SpaceType.AUDITORIUM,
                AreaFactory.sampleArea()
        );
    }

    public static Space sampleSpaceWithHardcodedUuid() {
        return new Space(
                UUID.fromString("8215f714-1bd5-4a17-bbef-6aa9396775a8"),
                "IVO",
                100,
                SpaceType.AUDITORIUM,
                AreaFactory.sampleAreaWithHardcodedUuid()
        );
    }
}
