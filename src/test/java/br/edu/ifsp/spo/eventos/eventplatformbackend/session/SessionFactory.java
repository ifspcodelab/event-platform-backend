package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SessionFactory {

    public static Session sampleSession() {
        return new Session(
                UUID.fromString("f08687d7-733d-4c4d-b1e2-3c1e8366bed8"),
                "Sessão 1",
                20,
                0,
                "Mensagem de cancelamento",
                false,
                ActivityFactory.sampleActivity(),
                SessionFactory.sampleSessionScheduleList()
        );
    }

    public static List<SessionSchedule> sampleSessionScheduleList() {
        return List.of(
                new SessionSchedule(
                        LocalDateTime.of(2023, 1, 9, 10, 0, 0),
                        LocalDateTime.of(2023, 1, 9, 11, 45, 0),
                        "",
                        LocationFactory.sampleLocation(),
                        AreaFactory.sampleArea(),
                        SpaceFactory.sampleSpaceWithHardcodedUuid()
                ));
    }
}
