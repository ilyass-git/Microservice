package emsi.ma.contratservice.domain.entity;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tenants")
public class Tenant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long contractId;
    private Long userId;
    private Long roomId;
}






