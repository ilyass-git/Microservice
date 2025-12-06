package emsi.ma.contratservice.client;

import emsi.ma.contratservice.client.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Client Feign pour communiquer avec le service Annonce
 * 
 * Cette interface permet au Contrat Service d'appeler les APIs du Annonce Service
 * pour vérifier l'existence et la disponibilité des chambres avant de créer des tenants.
 */
@FeignClient(name = "annonce-service", contextId = "room-service-client", path = "/api/rooms")
public interface RoomServiceClient {

    /**
     * Récupère une chambre par son ID
     * 
     * @param id L'ID de la chambre
     * @return ResponseEntity contenant la chambre ou 404 si non trouvée
     */
    @GetMapping("/{id}")
    ResponseEntity<RoomDto> getRoomById(@PathVariable Long id);

    /**
     * Met à jour la disponibilité d'une chambre
     * 
     * @param id L'ID de la chambre
     * @param isAvailable Nouvelle disponibilité
     * @return ResponseEntity
     */
    @PutMapping("/{id}/availability")
    ResponseEntity<Void> updateAvailability(@PathVariable Long id, @RequestBody Boolean isAvailable);
}

