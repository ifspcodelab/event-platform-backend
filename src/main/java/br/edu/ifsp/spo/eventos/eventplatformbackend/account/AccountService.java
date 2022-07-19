package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import org.springframework.stereotype.Service;

@Service
public class AccountService {
    public Account login(LoginCreateDto loginCreateDto){
        Account account = new Account (loginCreateDto.getEmail(), loginCreateDto.getPassword(), "ADMIN");

        return account;
    }
}
