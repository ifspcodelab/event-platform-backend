package br.edu.ifsp.spo.eventos.eventplatformbackend.account.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    public MyDataDto getUserByAccessToken(String accessToken) {
        String name = "Gabriel menino";
        String email = "gabrielmenino@mail.com";
        String cpf = "330.657.891-55";

        MyDataDto myDataDto = new MyDataDto(name, email, cpf);

        return myDataDto;
    }
}
