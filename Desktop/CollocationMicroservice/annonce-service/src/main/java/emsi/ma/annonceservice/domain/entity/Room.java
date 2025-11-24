package emsi.ma.annonceservice.domain.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rooms")
public class Room {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long propertyId;
    private String name;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    private Boolean isAvailable;
}

