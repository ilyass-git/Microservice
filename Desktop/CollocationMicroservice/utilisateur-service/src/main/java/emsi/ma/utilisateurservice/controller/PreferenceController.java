package emsi.ma.utilisateurservice.controller;

import emsi.ma.utilisateurservice.domain.dto.PreferenceDto;
import emsi.ma.utilisateurservice.domain.entity.Preference;
import emsi.ma.utilisateurservice.mapper.PreferenceMapper;
import emsi.ma.utilisateurservice.service.IPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final IPreferenceService preferenceService;
    private final PreferenceMapper preferenceMapper;

    @PostMapping
    public ResponseEntity<PreferenceDto> create(@RequestBody PreferenceDto preferenceDto) {
        Preference preference = preferenceMapper.toEntity(preferenceDto);
        Preference created = preferenceService.create(preference);
        return ResponseEntity.status(HttpStatus.CREATED).body(preferenceMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreferenceDto> getById(@PathVariable Long id) {
        return preferenceService.getById(id)
                .map(preference -> ResponseEntity.ok(preferenceMapper.toDto(preference)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PreferenceDto> getByUserId(@PathVariable Long userId) {
        return preferenceService.getByUserId(userId)
                .map(preference -> ResponseEntity.ok(preferenceMapper.toDto(preference)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PreferenceDto>> getAll() {
        List<PreferenceDto> preferences = preferenceService.getAll().stream()
                .map(preferenceMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(preferences);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreferenceDto> update(@PathVariable Long id, @RequestBody PreferenceDto preferenceDto) {
        Preference preference = preferenceMapper.toEntity(preferenceDto);
        Preference updated = preferenceService.update(id, preference);
        return ResponseEntity.ok(preferenceMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        preferenceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}






