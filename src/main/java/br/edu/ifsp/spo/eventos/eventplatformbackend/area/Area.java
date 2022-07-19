package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "areas")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Area {

    @Id
    private UUID id;
    private String name;
    private String reference;
    @ManyToOne
    private Location location;

    public Area(String name, String reference, Location location) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.reference = reference;
        this.location = location;
    }
}
