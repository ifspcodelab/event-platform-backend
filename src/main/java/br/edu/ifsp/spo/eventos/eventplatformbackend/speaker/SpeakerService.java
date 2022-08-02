package br.edu.ifsp.spo.eventos.eventplatformbackend.speaker;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class SpeakerService {
    private final SpeakerRepository speakerRepository;
    private final AccountRepository accountRepository;

    public Speaker create(SpeakerCreateDto dto) {
        if(speakerRepository.existsByCpf(dto.getCpf())) {
            throw new ResourceAlreadyExistsException(ResourceName.SPEAKER, "cpf", dto.getCpf());
        }

        if(speakerRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException(ResourceName.SPEAKER, "email", dto.getEmail());
        }

        if(dto.getAccountId() != null) {
            Account account = accountRepository
                .findById(dto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACCOUNT, dto.getAccountId()));

            Speaker speaker = dtoToSpeaker(dto);
            speaker.setAccount(account);
            return speakerRepository.save(speaker);
        }

        return speakerRepository.save(dtoToSpeaker(dto));
    }

    private Speaker dtoToSpeaker(SpeakerCreateDto dto) {
        return new Speaker(
            dto.getName(),
            dto.getEmail(),
            dto.getCpf(),
            dto.getCurriculum(),
            dto.getLattes(),
            dto.getLinkedin(),
            dto.getPhoneNumber()
        );
    }
}
