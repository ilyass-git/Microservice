package emsi.ma.annonceservice.service;

import emsi.ma.annonceservice.domain.entity.Room;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IRoomService {
    Room create(Room room);
    Optional<Room> getById(Long id);
    List<Room> getByPropertyId(Long propertyId);
    List<Room> getAvailableRooms();
    List<Room> getByPriceLessThan(BigDecimal maxPrice);
    Room update(Long id, Room room);
    void delete(Long id);
}

