package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventFactory;

import java.util.UUID;

public class ActivityFactory {
    public static Activity sampleActivity() {
        return new Activity(
                UUID.fromString("abc524f4-744c-46a7-af32-72cdc3094048"),
                "Curso de Java",
                "curso-de-java",
                "Curso de JavaCurso de JavaCurso de JavaCurso de Java",
                ActivityType.COURSE,
                EventStatus.DRAFT,
                ActivityModality.IN_PERSON,
                true,
                90,
                15,
                null,
                EventFactory.sampleEvent(),
                SubeventFactory.sampleSubevent()
        );
    }

    public static Activity sampleActivityRandomId() {
        return new Activity(
                UUID.randomUUID(),
                "Curso de Java",
                "curso-de-java",
                "Curso de JavaCurso de JavaCurso de JavaCurso de Java",
                ActivityType.COURSE,
                EventStatus.DRAFT,
                ActivityModality.IN_PERSON,
                true,
                90,
                15,
                null,
                EventFactory.sampleEventWithRandomId(),
                SubeventFactory.sampleSubeventWithRandomId()
        );
    }
}
