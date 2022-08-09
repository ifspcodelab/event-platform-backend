package br.edu.ifsp.spo.eventos.eventplatformbackend.users;


import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping()
    public ResponseEntity<Page<UserDto>> index(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cpf,
            @PageableDefault(page = 0, size = 20, sort="registrationTimestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        if (name != null){
            Page<Account> users = userService.findAllByName(pageable, name);
            return ResponseEntity.ok(users.map(userMapper::to));
        }

        if (email != null){
            Page<Account> users = userService.findAllByEmail(pageable, email);
            return ResponseEntity.ok(users.map(userMapper::to));
        }
        if (cpf != null){
            Page<Account> users = userService.findAllByCpf(pageable, cpf);
            return ResponseEntity.ok(users.map(userMapper::to));
        }


        Page<Account> users = userService.findAll(pageable);
        return ResponseEntity.ok(users.map(userMapper::to));
    }


    @GetMapping("{userId}")
    public ResponseEntity<UserDto> show(@PathVariable UUID userId) {
        Account user = userService.findById(userId);
        UserDto userDto = userMapper.to(user);
        return ResponseEntity.ok(userDto);
    }


    @PutMapping("{userId}")
    public ResponseEntity<UserDto> update(@PathVariable UUID userId, @RequestBody @Valid UserUpdateDto dto) {
        Account user = userService.update(userId, dto);
        UserDto userDto = userMapper.to(user);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

}
