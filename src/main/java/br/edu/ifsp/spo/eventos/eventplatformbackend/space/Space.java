package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
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
@Table(name = "spaces")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Space implements Diffable<Space> {
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

    @Override
    public DiffResult<Space> diff(Space updatedSpace) {
        return new DiffBuilder<>(this, updatedSpace, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Nome", this.name, updatedSpace.name)
                .append("Capacidade", this.capacity, updatedSpace.capacity)
                .append("Tipo", this.type, updatedSpace.type)
                .build();
    }
}
