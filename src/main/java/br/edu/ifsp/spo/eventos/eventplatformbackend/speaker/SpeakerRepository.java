package br.edu.ifsp.spo.eventos.eventplatformbackend.speaker;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpeakerRepository extends JpaRepository<Speaker, UUID> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByCpfAndIdNot(String cpf, UUID speakerId);
    boolean existsByEmailAndIdNot(String email, UUID speakerId);
    Optional<Speaker> findByCpf(String cpf);
}
