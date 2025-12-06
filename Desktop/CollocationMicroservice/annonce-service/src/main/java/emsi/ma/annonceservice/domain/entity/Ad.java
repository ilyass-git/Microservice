package emsi.ma.annonceservice.domain.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ads")
public class Ad {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long propertyId;
    private Long roomId; // null si annonce globale

    private String title;

    @Column(length = 2000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "ad_photo_urls", joinColumns = @JoinColumn(name = "ad_id"))
    @Column(name = "photo_urls")
    private List<String> photoUrls;

    private Long ownerId;

    @Enumerated(EnumType.STRING)
    private AdStatus status;
}






