package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/accounts")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("login")
    public ResponseEntity<TokensDto> login(@Valid @RequestBody LoginCreateDto loginCreateDto) {
        TokensDto tokensDto = authenticationService.login(loginCreateDto);

        return new ResponseEntity<>(tokensDto, HttpStatus.OK);
    }

    @PostMapping("refresh-token-rotation")
    public ResponseEntity<TokensDto> refreshTokenRotation(@Valid @RequestBody RefreshTokenRotateDto refreshTokenRotateDto) {
        TokensDto tokensDto = authenticationService.rotateRefreshToken(refreshTokenRotateDto);

        return new ResponseEntity<>(tokensDto, HttpStatus.OK);
    }

    @DeleteMapping("logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken) {
        authenticationService.logout(accessToken.replace("Bearer ", ""));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
