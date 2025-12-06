package emsi.ma.contratservice.domain.dto;

import emsi.ma.contratservice.domain.entity.ContractStatus;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

@Value
public class ContractDto implements Serializable {
    Long id;
    Long propertyId;
    LocalDate startDate;
    LocalDate endDate;
    ContractStatus status;
}






