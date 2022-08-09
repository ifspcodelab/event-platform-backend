package br.edu.ifsp.spo.eventos.eventplatformbackend.users;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto to(Account account);

    List<UserDto> to(List<Account> users);

}
