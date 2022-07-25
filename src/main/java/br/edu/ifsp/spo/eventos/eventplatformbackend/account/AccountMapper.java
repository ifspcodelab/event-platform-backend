package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDto to(Account account);
}