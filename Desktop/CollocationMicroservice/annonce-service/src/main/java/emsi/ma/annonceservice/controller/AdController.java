package emsi.ma.annonceservice.controller;

import emsi.ma.annonceservice.domain.dto.AdDto;
import emsi.ma.annonceservice.domain.entity.Ad;
import emsi.ma.annonceservice.domain.entity.AdStatus;
import emsi.ma.annonceservice.mapper.AdMapper;
import emsi.ma.annonceservice.service.IAdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdController {

    private final IAdService adService;
    private final AdMapper adMapper;

    @PostMapping
    public ResponseEntity<AdDto> create(@RequestBody AdDto adDto) {
        Ad ad = adMapper.toEntity(adDto);
        Ad created = adService.create(ad);
        return ResponseEntity.status(HttpStatus.CREATED).body(adMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdDto> getById(@PathVariable Long id) {
        return adService.getById(id)
                .map(ad -> ResponseEntity.ok(adMapper.toDto(ad)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/published")
    public ResponseEntity<List<AdDto>> getAllPublished() {
        List<AdDto> ads = adService.getAllPublished().stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<AdDto>> getByOwnerId(@PathVariable Long ownerId) {
        List<AdDto> ads = adService.getByOwnerId(ownerId).stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<AdDto>> getByPropertyId(@PathVariable Long propertyId) {
        List<AdDto> ads = adService.getByPropertyId(propertyId).stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AdDto>> searchByTitle(@RequestParam String keyword) {
        List<AdDto> ads = adService.searchByTitle(keyword).stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ads);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AdDto> updateStatus(@PathVariable Long id, @RequestParam AdStatus status) {
        Ad updated = adService.updateStatus(id, status);
        return ResponseEntity.ok(adMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

