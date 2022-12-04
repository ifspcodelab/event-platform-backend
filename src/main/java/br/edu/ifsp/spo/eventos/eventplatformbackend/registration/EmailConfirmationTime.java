package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailConfirmationTime {
    @Value("${registration.email-confirmation-time}")
    private String emailConfirmationTime;
}
