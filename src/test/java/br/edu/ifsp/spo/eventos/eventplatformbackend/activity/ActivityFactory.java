package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventFactory;

import java.util.List;

public class ActivityFactory {
    public static Activity sampleActivity() {
        return new Activity(
               "Atividade de exemplo",
                "slug-de-exemplo",
                "descrição de exemplo",
                ActivityType.SEMINAR,
                ActivityModality.IN_PERSON,
                true,
                50,
                10,
                EventFactory.sampleEvent(),
                SubeventFactory.sampleSubevent()
        );
    }
}
