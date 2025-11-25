package emsi.ma.utilisateurservice.domain.dto;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

@Value
public class PreferenceDto implements Serializable {
    Long id;
    Long userId;
    BigDecimal budget;
    String city;
    Boolean smokingAllowed;
}




