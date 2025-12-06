package emsi.ma.annonceservice.domain.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class PropertyDto implements Serializable {
    Long id;
    String title;
    String address;
    String city;
    String description;
    Long ownerId;
}






