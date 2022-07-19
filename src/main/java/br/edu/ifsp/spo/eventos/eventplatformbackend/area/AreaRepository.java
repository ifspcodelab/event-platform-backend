package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AreaRepository extends JpaRepository<Area, UUID> {
}
