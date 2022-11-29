package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityFactory;

import java.util.List;

public class SessionFactory {
    public static Session sampleSession() {
        return new Session(
                "Sessão de exemplo",
                20,
                ActivityFactory.sampleActivity(),
                List.of(SessionScheduleFactory.sampleSessionSchedule())
        );
    }
}
