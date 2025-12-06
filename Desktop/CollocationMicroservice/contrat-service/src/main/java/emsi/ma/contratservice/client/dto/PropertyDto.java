package emsi.ma.contratservice.client.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO pour représenter une propriété depuis le service Annonce
 * 
 * Cette classe doit correspondre exactement au PropertyDto du Annonce Service
 */
@Value
public class PropertyDto implements Serializable {
    Long id;
    String title;
    String address;
    String city;
    String description;
    Long ownerId;
}

