package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;

import java.time.LocalDate;

public class EventFactory {
    public static Event sampleEvent() {
        return new Event(
                "SEDICTEC 2022",
                "sedcitec-2022",
                "O evento aborda temas pertinentes ao desenvolvimento profissional dos alunos",
                "O evento aborda temas pertinentes ao desenvolvimento profissional dos alunos",
                "eventos@ifsp.edu.br",
                new Period(
                        LocalDate.of(2022,9, 1),
                        LocalDate.of(2022,9, 23)
                ),
                new Period(
                        LocalDate.of(2022,9, 19),
                        LocalDate.of(2022,9, 23)
                ),
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQRhTwr8uxAPMMBeL24_uEFjrePJ2bOGC7PRQ&usqp=CAU",
                "https://media.istockphoto.com/photos/happy-businesswoman-and-her-colleagues-applauding-on-an-education-in-picture-id1327425232?b=1&k=20&m=1327425232&s=170667a&w=0&h=yjRwDUwLz0VwitBG9_m_vx9PTCHk4YV4QuZBAbyjwSQ="
        );
    }
}
