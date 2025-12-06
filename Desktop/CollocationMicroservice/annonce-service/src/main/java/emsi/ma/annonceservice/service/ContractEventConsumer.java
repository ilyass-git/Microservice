package emsi.ma.annonceservice.service;

import emsi.ma.annonceservice.event.ContractEvent;
import emsi.ma.annonceservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Consommateur Kafka pour les événements de contrats
 * 
 * Ce service écoute les événements publiés par contrat-service et met à jour
 * automatiquement la disponibilité des chambres.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContractEventConsumer {

    private final RoomRepository roomRepository;

    @KafkaListener(topics = "contract-events", groupId = "annonce-service")
    public void handleContractEvent(ContractEvent event) {
        log.info("📥 [KAFKA] Événement reçu: {} pour contrat ID: {}", event.getEventType(), event.getContractId());
        
        try {
            switch (event.getEventType()) {
                case "TENANT_CREATED":
                    handleTenantCreated(event);
                    break;
                case "CONTRACT_TERMINATED":
                    handleContractTerminated(event);
                    break;
                default:
                    log.debug("Événement non géré: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("❌ [KAFKA] Erreur lors du traitement de l'événement {}: {}", 
                    event.getEventType(), e.getMessage(), e);
        }
    }

    /**
     * Gère la création d'un tenant
     * Marque la chambre comme non disponible si roomId est fourni
     */
    private void handleTenantCreated(ContractEvent event) {
        if (event.getRoomId() != null) {
            roomRepository.findById(event.getRoomId()).ifPresentOrElse(
                room -> {
                    if (room.getIsAvailable()) {
                        room.setIsAvailable(false);
                        roomRepository.save(room);
                        log.info("✅ [KAFKA] Chambre ID {} marquée comme non disponible (tenant créé)", 
                                event.getRoomId());
                    } else {
                        log.warn("⚠️ [KAFKA] Chambre ID {} était déjà non disponible", event.getRoomId());
                    }
                },
                () -> log.warn("⚠️ [KAFKA] Chambre ID {} non trouvée", event.getRoomId())
            );
        }
    }

    /**
     * Gère la résiliation d'un contrat
     * Marque la chambre comme disponible si roomId est fourni
     */
    private void handleContractTerminated(ContractEvent event) {
        if (event.getRoomId() != null) {
            roomRepository.findById(event.getRoomId()).ifPresentOrElse(
                room -> {
                    if (!room.getIsAvailable()) {
                        room.setIsAvailable(true);
                        roomRepository.save(room);
                        log.info("✅ [KAFKA] Chambre ID {} marquée comme disponible (contrat résilié)", 
                                event.getRoomId());
                    } else {
                        log.warn("⚠️ [KAFKA] Chambre ID {} était déjà disponible", event.getRoomId());
                    }
                },
                () -> log.warn("⚠️ [KAFKA] Chambre ID {} non trouvée", event.getRoomId())
            );
        }
    }
}

