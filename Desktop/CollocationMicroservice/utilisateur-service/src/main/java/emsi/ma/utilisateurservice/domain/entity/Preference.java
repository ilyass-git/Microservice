package emsi.ma.utilisateurservice.domain.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "preferences")
public class Preference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Column(precision = 12, scale = 2)
    private BigDecimal budget;
    private String city;
    private Boolean smokingAllowed;
}






