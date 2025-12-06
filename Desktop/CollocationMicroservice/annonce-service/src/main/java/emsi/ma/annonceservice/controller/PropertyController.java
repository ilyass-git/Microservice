package emsi.ma.annonceservice.controller;

import emsi.ma.annonceservice.domain.dto.PropertyDto;
import emsi.ma.annonceservice.domain.entity.Property;
import emsi.ma.annonceservice.mapper.PropertyMapper;
import emsi.ma.annonceservice.service.IPropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final IPropertyService propertyService;
    private final PropertyMapper propertyMapper;

    @PostMapping
    public ResponseEntity<PropertyDto> create(@RequestBody PropertyDto propertyDto) {
        Property property = propertyMapper.toEntity(propertyDto);
        Property created = propertyService.create(property);
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDto> getById(@PathVariable Long id) {
        return propertyService.getById(id)
                .map(property -> ResponseEntity.ok(propertyMapper.toDto(property)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PropertyDto>> getAll() {
        List<PropertyDto> properties = propertyService.getAll().stream()
                .map(propertyMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<PropertyDto>> getByCity(@PathVariable String city) {
        List<PropertyDto> properties = propertyService.getByCity(city).stream()
                .map(propertyMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PropertyDto>> getByOwnerId(@PathVariable Long ownerId) {
        List<PropertyDto> properties = propertyService.getByOwnerId(ownerId).stream()
                .map(propertyMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(properties);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDto> update(@PathVariable Long id, @RequestBody PropertyDto propertyDto) {
        Property property = propertyMapper.toEntity(propertyDto);
        Property updated = propertyService.update(id, property);
        return ResponseEntity.ok(propertyMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}






