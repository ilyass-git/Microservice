package emsi.ma.annonceservice.repository;

import emsi.ma.annonceservice.domain.entity.Ad;
import emsi.ma.annonceservice.domain.entity.AdStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByStatus(AdStatus status);
    List<Ad> findByOwnerId(Long ownerId);
    List<Ad> findByPropertyId(Long propertyId);
    List<Ad> findByRoomId(Long roomId);
    List<Ad> findByTitleContainingIgnoreCase(String keyword);
}

