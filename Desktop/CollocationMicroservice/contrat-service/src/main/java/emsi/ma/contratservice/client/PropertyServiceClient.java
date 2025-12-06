package emsi.ma.contratservice.client;

import emsi.ma.contratservice.client.dto.PropertyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client Feign pour communiquer avec le service Annonce
 * 
 * Cette interface permet au Contrat Service d'appeler les APIs du Annonce Service
 * pour vérifier l'existence des propriétés avant de créer des contrats.
 */
@FeignClient(name = "annonce-service", contextId = "property-service-client", path = "/api/properties")
public interface PropertyServiceClient {

    /**
     * Récupère une propriété par son ID
     * 
     * Cette méthode fait un appel HTTP GET vers annonce-service/api/properties/{id}
     * Eureka résout automatiquement le nom "annonce-service" en URL
     * 
     * @param id L'ID de la propriété
     * @return ResponseEntity contenant la propriété ou 404 si non trouvée
     */
    @GetMapping("/{id}")
    ResponseEntity<PropertyDto> getPropertyById(@PathVariable Long id);
}

