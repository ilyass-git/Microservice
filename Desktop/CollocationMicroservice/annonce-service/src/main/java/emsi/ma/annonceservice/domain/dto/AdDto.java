package emsi.ma.annonceservice.domain.dto;

import emsi.ma.annonceservice.domain.entity.AdStatus;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Value
public class AdDto implements Serializable {
    Long id;
    Long propertyId;
    Long roomId;
    String title;
    String description;
    List<String> photoUrls;
    Long ownerId;
    AdStatus status;
}




