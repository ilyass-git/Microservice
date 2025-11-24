package emsi.ma.contratservice.client;

import emsi.ma.contratservice.client.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client Feign pour communiquer avec le service Utilisateur
 * 
 * Cette interface permet au Contrat Service d'appeler les APIs du Utilisateur Service
 * sans connaître l'URL exacte. Eureka résout automatiquement le nom du service.
 */
@FeignClient(name = "utilisateur-service", path = "/api/users")
public interface UserServiceClient {

    /**
     * Récupère un utilisateur par son ID
     * 
     * Cette méthode fait un appel HTTP GET vers utilisateur-service/api/users/{id}
     * Eureka résout automatiquement le nom "utilisateur-service" en URL
     * 
     * @param id L'ID de l'utilisateur
     * @return ResponseEntity contenant l'utilisateur ou 404 si non trouvé
     */
    @GetMapping("/{id}")
    ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id);
}

