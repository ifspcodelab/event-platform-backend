package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    boolean existsByName(String name);
    List<Location> findAll();
}
