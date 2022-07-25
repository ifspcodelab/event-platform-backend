package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenRotationDto {
    private String refreshToken;
}
