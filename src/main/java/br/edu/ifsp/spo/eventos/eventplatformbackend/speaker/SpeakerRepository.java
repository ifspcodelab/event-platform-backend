package br.edu.ifsp.spo.eventos.eventplatformbackend.speaker;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpeakerRepository extends JpaRepository<Speaker, UUID> {
}
