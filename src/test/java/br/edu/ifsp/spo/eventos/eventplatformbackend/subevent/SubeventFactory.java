package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

public class SubeventFactory {
    public static Subevent sampleSubevent() {
        return new Subevent(
                UUID.fromString("0d87a5be-5448-42ac-bb3e-c94d2640c6f9"),
                "Subeventos de computação",
                "subeventos-de-computacao",
                "Subeventos de computaçãoSubeventos de computaçãoSubeventos de computação",
                "Subeventos de ComputaçãoSubeventos de ComputaçãoSubeventos de ComputaçãoSubeventos de ComputaçãoSubeventos de Computação",
                "Contato subeventos de computaçãoContato subeventos de computaçãoContato subeventos de computação",
                null,
                new Period(LocalDate.of(2023, Month.JANUARY,1), LocalDate.of(2023, Month.JANUARY,31)),
                "",
                "",
                br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventFactory.sampleEvent(),
                EventStatus.DRAFT,
                0
        );
    }

    public static Subevent sampleSubeventWithRandomId() {
        return new Subevent(
                UUID.randomUUID(),
                "Subeventos de computação",
                "subeventos-de-computacao",
                "Subeventos de computaçãoSubeventos de computaçãoSubeventos de computação",
                "Subeventos de ComputaçãoSubeventos de ComputaçãoSubeventos de ComputaçãoSubeventos de ComputaçãoSubeventos de Computação",
                "Contato subeventos de computaçãoContato subeventos de computaçãoContato subeventos de computação",
                null,
                new Period(LocalDate.of(2023, Month.JANUARY,1), LocalDate.of(2023, Month.JANUARY,31)),
                "",
                "",
                br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventFactory.sampleEvent(),
                EventStatus.DRAFT,
                0
        );
    }
}
