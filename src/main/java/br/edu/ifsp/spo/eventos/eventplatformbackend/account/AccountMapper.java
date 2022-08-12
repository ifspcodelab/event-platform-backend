package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDto to(Account account);



}
