package emsi.ma.annonceservice.repository;

import emsi.ma.annonceservice.domain.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByPropertyId(Long propertyId);
    List<Room> findByIsAvailableTrue();
    List<Room> findByPriceLessThan(BigDecimal price);
    List<Room> findByPropertyIdAndIsAvailableTrue(Long propertyId);
}






