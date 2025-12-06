package emsi.ma.utilisateurservice.controller;

import emsi.ma.utilisateurservice.domain.dto.ProfileDto;
import emsi.ma.utilisateurservice.domain.entity.Profile;
import emsi.ma.utilisateurservice.mapper.ProfileMapper;
import emsi.ma.utilisateurservice.service.IProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final IProfileService profileService;
    private final ProfileMapper profileMapper;

    @PostMapping
    public ResponseEntity<ProfileDto> create(@RequestBody ProfileDto profileDto) {
        Profile profile = profileMapper.toEntity(profileDto);
        Profile created = profileService.create(profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getById(@PathVariable Long id) {
        return profileService.getById(id)
                .map(profile -> ResponseEntity.ok(profileMapper.toDto(profile)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ProfileDto> getByUserId(@PathVariable Long userId) {
        return profileService.getByUserId(userId)
                .map(profile -> ResponseEntity.ok(profileMapper.toDto(profile)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProfileDto>> getAll() {
        List<ProfileDto> profiles = profileService.getAll().stream()
                .map(profileMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileDto> update(@PathVariable Long id, @RequestBody ProfileDto profileDto) {
        Profile profile = profileMapper.toEntity(profileDto);
        Profile updated = profileService.update(id, profile);
        return ResponseEntity.ok(profileMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        profileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}






