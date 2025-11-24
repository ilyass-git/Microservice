package emsi.ma.annonceservice.config;

import emsi.ma.annonceservice.domain.entity.Ad;
import emsi.ma.annonceservice.domain.entity.AdStatus;
import emsi.ma.annonceservice.domain.entity.Property;
import emsi.ma.annonceservice.domain.entity.Room;
import emsi.ma.annonceservice.repository.AdRepository;
import emsi.ma.annonceservice.repository.PropertyRepository;
import emsi.ma.annonceservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final AdRepository adRepository;

    @Override
    public void run(String... args) {
        log.info("Initialisation des données de test pour Annonce Service...");
        initializeProperties();
        initializeRooms();
        initializeAds();
        log.info("Initialisation des données terminée.");
    }

    private void initializeProperties() {
        if (propertyRepository.count() == 0) {
            log.info("Création des propriétés de test...");
            
            Property prop1 = Property.builder()
                    .title("Appartement 3 pièces - Centre-ville")
                    .address("123 Avenue Mohammed V")
                    .city("Casablanca")
                    .description("Bel appartement au centre de Casablanca, proche des transports et commerces")
                    .ownerId(1L)
                    .build();
            propertyRepository.save(prop1);

            Property prop2 = Property.builder()
                    .title("Villa avec jardin - Quartier résidentiel")
                    .address("45 Rue Hassan II")
                    .city("Rabat")
                    .description("Villa spacieuse avec jardin, idéale pour colocation")
                    .ownerId(2L)
                    .build();
            propertyRepository.save(prop2);

            Property prop3 = Property.builder()
                    .title("Studio moderne - Proche université")
                    .address("78 Boulevard Zerktouni")
                    .city("Casablanca")
                    .description("Studio récemment rénové, parfait pour étudiant")
                    .ownerId(3L)
                    .build();
            propertyRepository.save(prop3);

            log.info("3 propriétés créées.");
        } else {
            log.info("Des propriétés existent déjà. Aucune création.");
        }
    }

    private void initializeRooms() {
        if (roomRepository.count() == 0) {
            log.info("Création des chambres de test...");
            
            var properties = propertyRepository.findAll();
            if (properties.size() >= 3) {
                Room room1 = Room.builder()
                        .propertyId(properties.get(0).getId())
                        .name("Chambre 1")
                        .price(new BigDecimal("2000.00"))
                        .isAvailable(true)
                        .build();
                roomRepository.save(room1);

                Room room2 = Room.builder()
                        .propertyId(properties.get(0).getId())
                        .name("Chambre 2")
                        .price(new BigDecimal("1800.00"))
                        .isAvailable(true)
                        .build();
                roomRepository.save(room2);

                Room room3 = Room.builder()
                        .propertyId(properties.get(1).getId())
                        .name("Chambre principale")
                        .price(new BigDecimal("3000.00"))
                        .isAvailable(true)
                        .build();
                roomRepository.save(room3);

                Room room4 = Room.builder()
                        .propertyId(properties.get(2).getId())
                        .name("Studio complet")
                        .price(new BigDecimal("2500.00"))
                        .isAvailable(false)
                        .build();
                roomRepository.save(room4);

                log.info("4 chambres créées.");
            }
        } else {
            log.info("Des chambres existent déjà. Aucune création.");
        }
    }

    private void initializeAds() {
        if (adRepository.count() == 0) {
            log.info("Création des annonces de test...");
            
            var properties = propertyRepository.findAll();
            var rooms = roomRepository.findAll();
            
            if (properties.size() >= 2 && rooms.size() >= 2) {
                Ad ad1 = Ad.builder()
                        .propertyId(properties.get(0).getId())
                        .roomId(rooms.get(0).getId())
                        .title("Chambre disponible - Centre Casablanca")
                        .description("Chambre spacieuse et lumineuse dans appartement partagé. Proche transports et commerces.")
                        .photoUrls(Arrays.asList("https://example.com/photo1.jpg", "https://example.com/photo2.jpg"))
                        .ownerId(1L)
                        .status(AdStatus.PUBLISHED)
                        .build();
                adRepository.save(ad1);

                Ad ad2 = Ad.builder()
                        .propertyId(properties.get(0).getId())
                        .roomId(null)
                        .title("Appartement complet à louer")
                        .description("Appartement 3 pièces disponible pour colocation. Tous les équipements inclus.")
                        .photoUrls(Arrays.asList("https://example.com/photo3.jpg"))
                        .ownerId(1L)
                        .status(AdStatus.PUBLISHED)
                        .build();
                adRepository.save(ad2);

                Ad ad3 = Ad.builder()
                        .propertyId(properties.get(1).getId())
                        .roomId(rooms.get(2).getId())
                        .title("Chambre avec jardin - Rabat")
                        .description("Chambre dans villa avec accès au jardin. Quartier calme et résidentiel.")
                        .photoUrls(Arrays.asList("https://example.com/photo4.jpg"))
                        .ownerId(2L)
                        .status(AdStatus.DRAFT)
                        .build();
                adRepository.save(ad3);

                log.info("3 annonces créées.");
            }
        } else {
            log.info("Des annonces existent déjà. Aucune création.");
        }
    }
}

