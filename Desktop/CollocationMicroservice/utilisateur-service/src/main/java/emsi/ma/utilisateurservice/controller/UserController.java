package emsi.ma.utilisateurservice.controller;

import emsi.ma.utilisateurservice.domain.dto.UserCreateDto;
import emsi.ma.utilisateurservice.domain.dto.UserDto;
import emsi.ma.utilisateurservice.domain.dto.UserResponseDto;
import emsi.ma.utilisateurservice.domain.entity.User;
import emsi.ma.utilisateurservice.mapper.UserMapper;
import emsi.ma.utilisateurservice.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final IUserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody UserCreateDto userCreateDto) {
        User user = userMapper.toEntityFromCreate(userCreateDto);
        User created = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponseDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        // Cet endpoint peut être appelé directement ou via communication inter-service (Feign)
        log.info("📥 [APPEL REÇU] GET /api/users/{} - Peut être depuis un autre service via Feign", id);
        return userService.getById(id)
                .map(user -> {
                    log.info("✅ Utilisateur trouvé: {} {} (ID: {})", user.getPrenom(), user.getNom(), user.getId());
                    return ResponseEntity.ok(userMapper.toResponseDto(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        List<UserResponseDto> users = userService.getAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getByEmail(@PathVariable String email) {
        return userService.getByEmail(email)
                .map(user -> ResponseEntity.ok(userMapper.toResponseDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id, @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User updated = userService.update(id, user);
        return ResponseEntity.ok(userMapper.toResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

