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

    //TODO: refresh token rotation [Endpoint: "refresh-token-rotation"]
    //TODO: pegar/passar o refreshToken no body da requisição (dto, etc)
    //TODO: (service) verificar a validade do refreshToken...
    //TODO: 0. o refreshToken enviado existe na base de dados?
    //TODO: 1. é um token legítimo (emitido por nós)?
    //TODO: 2. o token está dentro do prazo de expiração?
    //TODO: 3. caso sim... apagar o refresh token antigo...
    //TODO: 4. ...e gerar um novo refresh token
    //TODO: 5. caso não... mandar um conflito(?) (procurar o status certo)
    //TODO: 6. fazer o log no ExceptionHandler (classes RefreshTokenExceptionType e RefreshTokenException)

    //TODO: logout (invalidação do refreshToken) [Endpoint: "logout"]
    //TODO: 0. o refreshToken existe?
    //TODO: 1. caso sim, apagar o refreshToken
    //TODO: 2. fazer o log (info logout, caso necessário criar type, etc)

    @DeleteMapping("logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken) {
        authenticationService.logout(accessToken.replace("Bearer ", ""));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
