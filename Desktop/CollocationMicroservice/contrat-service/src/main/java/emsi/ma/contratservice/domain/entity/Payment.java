package emsi.ma.contratservice.domain.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long contractId;
    @Column(precision = 12, scale = 2)
    private BigDecimal amount;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private PaymentType type;
}






