package emsi.ma.contratservice.domain.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class TenantDto implements Serializable {
    Long id;
    Long contractId;
    Long userId;
    Long roomId;
}

