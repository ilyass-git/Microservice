package emsi.ma.contratservice.domain.dto;

import emsi.ma.contratservice.domain.entity.PaymentType;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Value
public class PaymentDto implements Serializable {
    Long id;
    Long contractId;
    BigDecimal amount;
    LocalDate dueDate;
    PaymentType type;
}

