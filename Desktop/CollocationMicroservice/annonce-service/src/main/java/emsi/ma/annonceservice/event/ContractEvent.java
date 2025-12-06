package emsi.ma.annonceservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Événement Kafka pour les contrats (reçu depuis contrat-service)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractEvent implements Serializable {
    private String eventType;
    private Long contractId;
    private Long propertyId;
    private Long roomId;
    private LocalDateTime timestamp;
    private Object data;
}

