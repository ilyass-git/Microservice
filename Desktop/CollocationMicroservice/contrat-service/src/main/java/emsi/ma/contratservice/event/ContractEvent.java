package emsi.ma.contratservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Événement Kafka pour les contrats
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractEvent implements Serializable {
    private String eventType; // CONTRACT_CREATED, CONTRACT_ACTIVATED, CONTRACT_TERMINATED, PAYMENT_RECEIVED
    private Long contractId;
    private Long propertyId;
    private Long roomId;
    private LocalDateTime timestamp;
    private Object data; // Données supplémentaires selon le type d'événement
}

