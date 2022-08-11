package br.edu.ifsp.spo.eventos.eventplatformbackend.account.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/accounts")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("my-data")
    public ResponseEntity<MyDataDto> show(@RequestHeader("Authorization") String accessToken) {
        MyDataDto myDataDto = userService.getUserByAccessToken(accessToken.replace("Bearer ", ""));

        return new ResponseEntity<>(myDataDto, HttpStatus.OK);
    }
}
