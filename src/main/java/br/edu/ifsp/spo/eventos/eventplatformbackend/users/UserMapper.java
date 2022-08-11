package br.edu.ifsp.spo.eventos.eventplatformbackend.users;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountManagementDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    AccountManagementDto to(Account account);

    List<AccountManagementDto> to(List<Account> users);

}
