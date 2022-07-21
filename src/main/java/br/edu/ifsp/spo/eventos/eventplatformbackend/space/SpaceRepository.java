package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpaceRepository extends JpaRepository<Space, UUID> {
    boolean existsByNameAndArea(String name, Area area);
    boolean existsByNameAndIdNot(String name, UUID spaceId);
    boolean existsByAreaId(UUID areaId);
    List<Space> findAllByAreaId(UUID areaId);
}
