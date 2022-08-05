package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDto to(Account account);
    List<AccountDto> to(List<Account> accounts);
}
