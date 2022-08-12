package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
}
