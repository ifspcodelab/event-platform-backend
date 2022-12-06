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
                UUID.fromString("4e981b82-152b-4e7c-b79e-90eea7a74ee6"),
                "IVO",
                100,
                SpaceType.AUDITORIUM,
                AreaFactory.sampleAreaWithHardcodedLocationUuid()
        );
    }
}
