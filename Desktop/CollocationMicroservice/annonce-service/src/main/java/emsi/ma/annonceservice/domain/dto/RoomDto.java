package emsi.ma.annonceservice.domain.dto;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

@Value
public class RoomDto implements Serializable {
    Long id;
    Long propertyId;
    String name;
    BigDecimal price;
    Boolean isAvailable;
}

