package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "areas")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Area implements Diffable<Area> {
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

    @Override
    public DiffResult<Area> diff(Area updatedArea) {
        return new DiffBuilder<>(this, updatedArea, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Nome", this.name, updatedArea.name)
                .append("Referência", this.reference, updatedArea.reference)
                .build();
    }
}
