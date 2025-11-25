package emsi.ma.annonceservice.controller;

import emsi.ma.annonceservice.domain.dto.RoomDto;
import emsi.ma.annonceservice.domain.entity.Room;
import emsi.ma.annonceservice.mapper.RoomMapper;
import emsi.ma.annonceservice.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final IRoomService roomService;
    private final RoomMapper roomMapper;

    @PostMapping
    public ResponseEntity<RoomDto> create(@RequestBody RoomDto roomDto) {
        Room room = roomMapper.toEntity(roomDto);
        Room created = roomService.create(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getById(@PathVariable Long id) {
        return roomService.getById(id)
                .map(room -> ResponseEntity.ok(roomMapper.toDto(room)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<RoomDto>> getByPropertyId(@PathVariable Long propertyId) {
        List<RoomDto> rooms = roomService.getByPropertyId(propertyId).stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomDto>> getAvailableRooms() {
        List<RoomDto> rooms = roomService.getAvailableRooms().stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/price")
    public ResponseEntity<List<RoomDto>> getByPriceLessThan(@RequestParam BigDecimal maxPrice) {
        List<RoomDto> rooms = roomService.getByPriceLessThan(maxPrice).stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rooms);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> update(@PathVariable Long id, @RequestBody RoomDto roomDto) {
        Room room = roomMapper.toEntity(roomDto);
        Room updated = roomService.update(id, room);
        return ResponseEntity.ok(roomMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}




