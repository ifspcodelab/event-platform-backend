package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

public class EventFactory {
    public static Event sampleEvent() {
        return new Event(
                UUID.fromString("d9bcff96-9efa-4c52-b519-bacac813aeda"),
                "Mês IFSP Codelab",
                "mes-ifsp-codelab",
                "Mês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP Codelab",
                "Mês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP Codelab",
                "Contato mês IFSP CodelabContato mês IFSP CodelabContato mês IFSP Codelab",
                new Period(LocalDate.of(2022, Month.DECEMBER,15), LocalDate.of(2022, Month.DECEMBER,31)),
                new Period(LocalDate.of(2023, Month.JANUARY,1), LocalDate.of(2023, Month.JANUARY,31)),
                "",
                "",
                EventStatus.DRAFT,
                null
        );
    }

    public static Event sampleEventWithRandomId() {
        return new Event(
                UUID.randomUUID(),
                "Mês IFSP Codelab",
                "mes-ifsp-codelab",
                "Mês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP Codelab",
                "Mês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP CodelabMês IFSP Codelab",
                "Contato mês IFSP CodelabContato mês IFSP CodelabContato mês IFSP Codelab",
                new Period(LocalDate.of(2022, Month.DECEMBER,15), LocalDate.of(2022, Month.DECEMBER,31)),
                new Period(LocalDate.of(2023, Month.JANUARY,1), LocalDate.of(2023, Month.JANUARY,31)),
                "",
                "",
                EventStatus.DRAFT,
                null
        );
    }
}
