package emsi.ma.contratservice.service.impl;

import emsi.ma.contratservice.client.RoomServiceClient;
import emsi.ma.contratservice.client.UserServiceClient;
import emsi.ma.contratservice.client.dto.RoomDto;
import emsi.ma.contratservice.client.dto.UserResponseDto;
import emsi.ma.contratservice.domain.entity.Tenant;
import emsi.ma.contratservice.repository.TenantRepository;
import emsi.ma.contratservice.service.ITenantService;
import emsi.ma.contratservice.service.ContractEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements ITenantService {

    private final TenantRepository tenantRepository;
    private final UserServiceClient userServiceClient; // Client Feign pour communication inter-service
    private final RoomServiceClient roomServiceClient; // Client Feign pour communication inter-service
    private final ContractEventProducer contractEventProducer; // Producer Kafka

    @Override
    public Tenant create(Tenant tenant) {
        // EXEMPLE DE COMMUNICATION INTER-SERVICE
        // Avant de créer un tenant, on vérifie que l'utilisateur existe dans le service Utilisateur
        log.info("🔗 [COMMUNICATION INTER-SERVICE] Vérification de l'existence de l'utilisateur ID: {}", tenant.getUserId());
        log.info("   Service appelant: contrat-service");
        log.info("   Service appelé: utilisateur-service");
        log.info("   Endpoint: GET /api/users/{}", tenant.getUserId());
        
        try {
            ResponseEntity<UserResponseDto> response =
                userServiceClient.getUserById(tenant.getUserId());
            
            // Vérifier le status code HTTP
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("❌ [COMMUNICATION ÉCHOUÉE] Status code: {} - Utilisateur ID {} non trouvé", 
                        response.getStatusCode().value(), tenant.getUserId());
                throw new RuntimeException("Utilisateur avec ID " + tenant.getUserId() + " n'existe pas (Status: " + response.getStatusCode() + ")");
            }
            
            // Vérifier que le body n'est pas null
            if (response.getBody() == null) {
                log.warn("❌ [COMMUNICATION ÉCHOUÉE] Réponse vide - Utilisateur ID {} non trouvé", tenant.getUserId());
                throw new RuntimeException("Utilisateur avec ID " + tenant.getUserId() + " n'existe pas");
            }
            
            var user = response.getBody();
            log.info("✅ [COMMUNICATION RÉUSSIE] Utilisateur trouvé: {} {} (ID: {})", 
                    user.getPrenom(), user.getNom(), user.getId());
            log.info("   Communication inter-service: contrat-service -> utilisateur-service");
            
        } catch (RuntimeException e) {
            // Si c'est déjà notre exception personnalisée, la relancer
            if (e.getMessage().contains("n'existe pas")) {
                throw e;
            }
            log.error("❌ [ERREUR COMMUNICATION] Erreur lors de la communication avec utilisateur-service: {}", e.getMessage());
            throw new RuntimeException("Impossible de vérifier l'utilisateur: " + e.getMessage(), e);
        }
        
        // Vérifier la chambre si roomId est fourni
        if (tenant.getRoomId() != null) {
            log.info("🔗 [COMMUNICATION INTER-SERVICE] Vérification de la chambre ID: {}", tenant.getRoomId());
            log.info("   Service appelant: contrat-service");
            log.info("   Service appelé: annonce-service");
            log.info("   Endpoint: GET /api/rooms/{}", tenant.getRoomId());
            
            try {
                ResponseEntity<RoomDto> roomResponse = roomServiceClient.getRoomById(tenant.getRoomId());
                
                if (!roomResponse.getStatusCode().is2xxSuccessful() || roomResponse.getBody() == null) {
                    log.warn("❌ [COMMUNICATION ÉCHOUÉE] Chambre ID {} non trouvée", tenant.getRoomId());
                    throw new RuntimeException("Chambre avec ID " + tenant.getRoomId() + " n'existe pas");
                }
                
                var room = roomResponse.getBody();
                
                // Vérifier que la chambre est disponible
                if (!room.getIsAvailable()) {
                    log.warn("❌ [CHAMBRE INDISPONIBLE] Chambre ID {} n'est pas disponible", tenant.getRoomId());
                    throw new RuntimeException("Chambre avec ID " + tenant.getRoomId() + " n'est pas disponible");
                }
                
                log.info("✅ [COMMUNICATION RÉUSSIE] Chambre trouvée: {} (ID: {}) - Disponible: {}", 
                        room.getName(), room.getId(), room.getIsAvailable());
                
                // Marquer la chambre comme non disponible
                log.info("🔄 [MISE À JOUR] Marquage de la chambre ID {} comme non disponible", tenant.getRoomId());
                roomServiceClient.updateAvailability(tenant.getRoomId(), false);
                log.info("✅ Chambre ID {} marquée comme non disponible", tenant.getRoomId());
                
            } catch (RuntimeException e) {
                if (e.getMessage().contains("n'existe pas") || e.getMessage().contains("pas disponible")) {
                    throw e;
                }
                log.error("❌ [ERREUR COMMUNICATION] Erreur lors de la communication avec annonce-service: {}", e.getMessage());
                throw new RuntimeException("Impossible de vérifier la chambre: " + e.getMessage(), e);
            }
        }
        
        log.info("✅ Création du tenant pour l'utilisateur ID: {} et chambre ID: {}", 
                tenant.getUserId(), tenant.getRoomId());
        Tenant savedTenant = tenantRepository.save(tenant);
        
        // Publier l'événement Kafka si une chambre est associée
        if (savedTenant.getRoomId() != null) {
            contractEventProducer.publishTenantCreated(savedTenant);
        }
        
        return savedTenant;
    }

    @Override
    public Optional<Tenant> getById(Long id) {
        return tenantRepository.findById(id);
    }

    @Override
    public List<Tenant> getAll() {
        return tenantRepository.findAll();
    }

    @Override
    public List<Tenant> getByContractId(Long contractId) {
        return tenantRepository.findAll().stream()
                .filter(t -> t.getContractId().equals(contractId))
                .toList();
    }

    @Override
    public List<Tenant> getByUserId(Long userId) {
        return tenantRepository.findAll().stream()
                .filter(t -> t.getUserId().equals(userId))
                .toList();
    }

    @Override
    public Tenant update(Long id, Tenant tenant) {
        tenant.setId(id);
        return tenantRepository.save(tenant);
    }

    @Override
    public void delete(Long id) {
        tenantRepository.deleteById(id);
    }
}

