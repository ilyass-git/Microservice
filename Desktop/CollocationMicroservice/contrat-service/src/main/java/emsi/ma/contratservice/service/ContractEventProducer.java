package emsi.ma.contratservice.service;

import emsi.ma.contratservice.domain.entity.Contract;
import emsi.ma.contratservice.domain.entity.Tenant;
import emsi.ma.contratservice.event.ContractEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service pour publier des événements Kafka liés aux contrats
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContractEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String CONTRACT_EVENTS_TOPIC = "contract-events";

    /**
     * Publie un événement de création de contrat
     */
    public void publishContractCreated(Contract contract) {
        ContractEvent event = ContractEvent.builder()
                .eventType("CONTRACT_CREATED")
                .contractId(contract.getId())
                .propertyId(contract.getPropertyId())
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(CONTRACT_EVENTS_TOPIC, event);
        log.info("📤 [KAFKA] Événement publié: CONTRACT_CREATED pour contrat ID: {}", contract.getId());
    }

    /**
     * Publie un événement d'activation de contrat
     */
    public void publishContractActivated(Contract contract) {
        ContractEvent event = ContractEvent.builder()
                .eventType("CONTRACT_ACTIVATED")
                .contractId(contract.getId())
                .propertyId(contract.getPropertyId())
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(CONTRACT_EVENTS_TOPIC, event);
        log.info("📤 [KAFKA] Événement publié: CONTRACT_ACTIVATED pour contrat ID: {}", contract.getId());
    }

    /**
     * Publie un événement de résiliation de contrat
     */
    public void publishContractTerminated(Contract contract) {
        ContractEvent event = ContractEvent.builder()
                .eventType("CONTRACT_TERMINATED")
                .contractId(contract.getId())
                .propertyId(contract.getPropertyId())
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(CONTRACT_EVENTS_TOPIC, event);
        log.info("📤 [KAFKA] Événement publié: CONTRACT_TERMINATED pour contrat ID: {}", contract.getId());
    }

    /**
     * Publie un événement de résiliation de contrat avec roomId spécifique
     */
    public void publishContractTerminated(Contract contract, Long roomId) {
        ContractEvent event = ContractEvent.builder()
                .eventType("CONTRACT_TERMINATED")
                .contractId(contract.getId())
                .propertyId(contract.getPropertyId())
                .roomId(roomId)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(CONTRACT_EVENTS_TOPIC, event);
        log.info("📤 [KAFKA] Événement publié: CONTRACT_TERMINATED pour contrat ID: {} (roomId: {})", 
                contract.getId(), roomId);
    }

    /**
     * Publie un événement de création de tenant (avec roomId)
     */
    public void publishTenantCreated(Tenant tenant) {
        ContractEvent event = ContractEvent.builder()
                .eventType("TENANT_CREATED")
                .contractId(tenant.getContractId())
                .roomId(tenant.getRoomId())
                .timestamp(LocalDateTime.now())
                .data(tenant.getId())
                .build();

        kafkaTemplate.send(CONTRACT_EVENTS_TOPIC, event);
        log.info("📤 [KAFKA] Événement publié: TENANT_CREATED pour tenant ID: {} (roomId: {})", 
                tenant.getId(), tenant.getRoomId());
    }
}

