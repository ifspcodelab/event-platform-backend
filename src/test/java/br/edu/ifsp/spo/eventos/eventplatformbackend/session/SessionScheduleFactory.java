package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceFactory;

import java.time.LocalDateTime;

public class SessionScheduleFactory {
    public static SessionSchedule sampleSessionSchedule() {
        return new SessionSchedule(

                LocalDateTime.of(2022, 9, 23, 14, 0, 0),
                LocalDateTime.of(2022, 9, 23, 16, 0, 0),
                "url",
                LocationFactory.sampleLocation(),
                AreaFactory.sampleArea(),
                SpaceFactory.sampleSpace()
        );
    }

    public static SessionSchedule sampleSessionSchedule2() {
        return new SessionSchedule(

                LocalDateTime.of(2022, 9, 21, 14, 0, 0),
                LocalDateTime.of(2022, 9, 22, 16, 0, 0),
                "url",
                LocationFactory.sampleLocation(),
                AreaFactory.sampleArea(),
                SpaceFactory.sampleSpace()
        );
    }
}
