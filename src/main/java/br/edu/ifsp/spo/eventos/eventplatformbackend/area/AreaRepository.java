package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AreaRepository extends JpaRepository<Area, UUID> {
    boolean existsByNameIgnoreCaseAndLocationId(String name, UUID locationId);
    boolean existsByNameAndLocationId(String name, UUID locationId);
    boolean existsByNameAndLocationIdAndIdNot(String name, UUID locationId, UUID areaId);
    List<Area> findAllByLocationId(UUID locationId);
    boolean existsByLocationId(UUID locationId);
}
