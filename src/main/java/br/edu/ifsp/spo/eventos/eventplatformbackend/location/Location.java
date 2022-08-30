package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "locations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Location implements Diffable<Location> {
    @Id
    private UUID id;
    private String name;
    private String address;

    public Location(String name, String address) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.address = address;
    }

    @Override
    public DiffResult<Location> diff(Location updatedLocation) {
        return new DiffBuilder<>(this, updatedLocation, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Nome", this.name, updatedLocation.name)
                .append("Endereço", this.address, updatedLocation.address)
                .build();
    }
}
