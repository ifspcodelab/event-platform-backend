package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name ="spaces")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Space {
    @Id
    private UUID id;
    private String name;
    private Integer capacity;
    @Enumerated(EnumType.STRING)
    private SpaceType type;
    @ManyToOne
    private Area area;

    public Space(String name, Integer capacity, SpaceType type, Area area) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.capacity = capacity;
        this.type = type;
        this.area = area;
    }
}
