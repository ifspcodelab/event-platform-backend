package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AreaRepository extends JpaRepository<Area, UUID> {
    boolean existsByNameAndLocation(String name, Location location);
    boolean existsByNameAndIdNot(String name, UUID areaId);
    List<Area> findAllByLocationId(UUID locationId);
    boolean existsByLocationId(UUID locationId);
}