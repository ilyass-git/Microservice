package emsi.ma.annonceservice.client;

import emsi.ma.annonceservice.client.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client Feign pour communiquer avec le service Utilisateur
 * 
 * Permet à Annonce Service d'appeler Utilisateur Service pour vérifier les propriétaires
 */
@FeignClient(name = "utilisateur-service", path = "/api/users")
public interface UserServiceClient {

    @GetMapping("/{id}")
    ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id);
}

