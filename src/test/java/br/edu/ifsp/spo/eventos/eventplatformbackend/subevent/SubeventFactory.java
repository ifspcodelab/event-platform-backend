package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventFactory;

import java.time.LocalDate;

public class SubeventFactory {
    public static Subevent sampleSubevent() {
        return new Subevent(
                "Semana da Química",
                "semana-da-quimica",
                "A semana da química tem como objetivo de difundir o conhecimento químico",
                "A semana da química tem como objetivo de difundir o conhecimento químico",
                "eventos@ifsp.edu.br",
                new Period(
                        LocalDate.of(2022,9, 19),
                        LocalDate.of(2022,9, 23)
                ),
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQRhTwr8uxAPMMBeL24_uEFjrePJ2bOGC7PRQ&usqp=CAU",
                "https://media.istockphoto.com/photos/happy-businesswoman-and-her-colleagues-applauding-on-an-education-in-picture-id1327425232?b=1&k=20&m=1327425232&s=170667a&w=0&h=yjRwDUwLz0VwitBG9_m_vx9PTCHk4YV4QuZBAbyjwSQ=",
                EventFactory.sampleEvent()
        );
    }
}
