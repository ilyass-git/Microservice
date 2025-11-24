package emsi.ma.annonceservice.service.impl;

import emsi.ma.annonceservice.client.UserServiceClient;
import emsi.ma.annonceservice.client.dto.UserResponseDto;
import emsi.ma.annonceservice.domain.entity.Ad;
import emsi.ma.annonceservice.domain.entity.AdStatus;
import emsi.ma.annonceservice.repository.AdRepository;
import emsi.ma.annonceservice.service.IAdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdServiceImpl implements IAdService {

    private final AdRepository adRepository;
    private final UserServiceClient userServiceClient; // Client Feign pour communication inter-service

    @Override
    public Ad create(Ad ad) {
        // EXEMPLE DE COMMUNICATION INTER-SERVICE
        // Vérifier que le propriétaire existe dans le service Utilisateur
        log.info("🔗 [COMMUNICATION INTER-SERVICE] Vérification du propriétaire ID: {}", ad.getOwnerId());
        log.info("   Service appelant: annonce-service");
        log.info("   Service appelé: utilisateur-service");
        log.info("   Endpoint: GET /api/users/{}", ad.getOwnerId());
        
        try {
            ResponseEntity<UserResponseDto> response =
                userServiceClient.getUserById(ad.getOwnerId());
            
            // Vérifier le status code HTTP
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("❌ [COMMUNICATION ÉCHOUÉE] Status code: {} - Propriétaire ID {} non trouvé", 
                        response.getStatusCode().value(), ad.getOwnerId());
                throw new RuntimeException("Propriétaire avec ID " + ad.getOwnerId() + " n'existe pas (Status: " + response.getStatusCode() + ")");
            }
            
            // Vérifier que le body n'est pas null
            if (response.getBody() == null) {
                log.warn("❌ [COMMUNICATION ÉCHOUÉE] Réponse vide - Propriétaire ID {} non trouvé", ad.getOwnerId());
                throw new RuntimeException("Propriétaire avec ID " + ad.getOwnerId() + " n'existe pas");
            }
            
            var owner = response.getBody();
            log.info("✅ [COMMUNICATION RÉUSSIE] Propriétaire trouvé: {} {} (ID: {})", 
                    owner.getPrenom(), owner.getNom(), owner.getId());
            log.info("   Communication inter-service: annonce-service -> utilisateur-service");
            
        } catch (RuntimeException e) {
            // Si c'est déjà notre exception personnalisée, la relancer
            if (e.getMessage().contains("n'existe pas")) {
                throw e;
            }
            log.error("❌ [ERREUR COMMUNICATION] Erreur lors de la communication avec utilisateur-service: {}", e.getMessage());
            throw new RuntimeException("Impossible de vérifier le propriétaire: " + e.getMessage(), e);
        }
        
        log.info("✅ Création de l'annonce pour le propriétaire ID: {}", ad.getOwnerId());
        return adRepository.save(ad);
    }

    @Override
    public Optional<Ad> getById(Long id) {
        return adRepository.findById(id);
    }

    @Override
    public List<Ad> getAllPublished() {
        return adRepository.findByStatus(AdStatus.PUBLISHED);
    }

    @Override
    public List<Ad> getByOwnerId(Long ownerId) {
        return adRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Ad> getByPropertyId(Long propertyId) {
        return adRepository.findByPropertyId(propertyId);
    }

    @Override
    public List<Ad> searchByTitle(String keyword) {
        return adRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Override
    public Ad updateStatus(Long id, AdStatus status) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));
        ad.setStatus(status);
        return adRepository.save(ad);
    }

    @Override
    public void delete(Long id) {
        adRepository.deleteById(id);
    }
}

