package emsi.ma.contratservice.client.dto;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO pour représenter une chambre depuis le service Annonce
 * 
 * Cette classe doit correspondre exactement au RoomDto du Annonce Service
 */
@Value
public class RoomDto implements Serializable {
    Long id;
    Long propertyId;
    String name;
    BigDecimal price;
    Boolean isAvailable;
}

