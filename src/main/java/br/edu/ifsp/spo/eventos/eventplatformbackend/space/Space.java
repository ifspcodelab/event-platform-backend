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
    private SpaceType spaceType;
    @ManyToOne
    private Area area;

    public Space(String name, Integer capacity, SpaceType spaceType, Area area) {
        this.name = name;
        this.capacity = capacity;
        this.spaceType = spaceType;
        this.area = area;
    }
}
