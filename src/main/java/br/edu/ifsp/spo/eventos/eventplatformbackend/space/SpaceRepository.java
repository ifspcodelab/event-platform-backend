package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpaceRepository extends JpaRepository<Space, UUID> {
    boolean existsByNameAndAreaId(String name, UUID area);
    boolean existsByNameAndAreaIdAndIdNot(String name, UUID areaId, UUID spaceId);
    boolean existsByAreaId(UUID areaId);
    List<Space> findAllByAreaId(UUID areaId);
}
