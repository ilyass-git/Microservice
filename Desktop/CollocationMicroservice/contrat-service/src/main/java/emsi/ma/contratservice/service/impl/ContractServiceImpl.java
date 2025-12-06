package emsi.ma.contratservice.service.impl;

import emsi.ma.contratservice.client.PropertyServiceClient;
import emsi.ma.contratservice.client.dto.PropertyDto;
import emsi.ma.contratservice.domain.entity.Contract;
import emsi.ma.contratservice.domain.entity.ContractStatus;
import emsi.ma.contratservice.domain.entity.Tenant;
import emsi.ma.contratservice.event.ContractEvent;
import emsi.ma.contratservice.repository.ContractRepository;
import emsi.ma.contratservice.repository.TenantRepository;
import emsi.ma.contratservice.service.IContractService;
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
public class ContractServiceImpl implements IContractService {

    private final ContractRepository contractRepository;
    private final TenantRepository tenantRepository;
    private final PropertyServiceClient propertyServiceClient; // Client Feign pour communication inter-service
    private final ContractEventProducer contractEventProducer; // Producer Kafka

    @Override
    public Contract create(Contract contract) {
        // EXEMPLE DE COMMUNICATION INTER-SERVICE
        // Avant de créer un contrat, on vérifie que la propriété existe dans le service Annonce
        log.info("🔗 [COMMUNICATION INTER-SERVICE] Vérification de l'existence de la propriété ID: {}", contract.getPropertyId());
        log.info("   Service appelant: contrat-service");
        log.info("   Service appelé: annonce-service");
        log.info("   Endpoint: GET /api/properties/{}", contract.getPropertyId());
        
        try {
            ResponseEntity<PropertyDto> response = propertyServiceClient.getPropertyById(contract.getPropertyId());
            
            // Vérifier le status code HTTP
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("❌ [COMMUNICATION ÉCHOUÉE] Status code: {} - Propriété ID {} non trouvée", 
                        response.getStatusCode().value(), contract.getPropertyId());
                throw new RuntimeException("Propriété avec ID " + contract.getPropertyId() + " n'existe pas (Status: " + response.getStatusCode() + ")");
            }
            
            // Vérifier que le body n'est pas null
            if (response.getBody() == null) {
                log.warn("❌ [COMMUNICATION ÉCHOUÉE] Réponse vide - Propriété ID {} non trouvée", contract.getPropertyId());
                throw new RuntimeException("Propriété avec ID " + contract.getPropertyId() + " n'existe pas");
            }
            
            var property = response.getBody();
            log.info("✅ [COMMUNICATION RÉUSSIE] Propriété trouvée: {} (ID: {})", 
                    property.getTitle(), property.getId());
            log.info("   Communication inter-service: contrat-service -> annonce-service");
            
        } catch (RuntimeException e) {
            // Si c'est déjà notre exception personnalisée, la relancer
            if (e.getMessage().contains("n'existe pas")) {
                throw e;
            }
            log.error("❌ [ERREUR COMMUNICATION] Erreur lors de la communication avec annonce-service: {}", e.getMessage());
            throw new RuntimeException("Impossible de vérifier la propriété: " + e.getMessage(), e);
        }
        
        log.info("✅ Création du contrat pour la propriété ID: {}", contract.getPropertyId());
        Contract savedContract = contractRepository.save(contract);
        
        // Publier l'événement Kafka
        contractEventProducer.publishContractCreated(savedContract);
        
        // Si le contrat est actif, publier aussi l'événement d'activation
        if (savedContract.getStatus() == ContractStatus.ACTIVE) {
            contractEventProducer.publishContractActivated(savedContract);
        }
        
        return savedContract;
    }

    @Override
    public Optional<Contract> getById(Long id) {
        return contractRepository.findById(id);
    }

    @Override
    public List<Contract> getAll() {
        return contractRepository.findAll();
    }

    @Override
    public List<Contract> getByPropertyId(Long propertyId) {
        return contractRepository.findAll().stream()
                .filter(c -> c.getPropertyId().equals(propertyId))
                .toList();
    }

    @Override
    public Contract update(Long id, Contract contract) {
        Contract existingContract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));
        
        ContractStatus oldStatus = existingContract.getStatus();
        contract.setId(id);
        Contract updatedContract = contractRepository.save(contract);
        
        // Publier un événement si le statut a changé
        if (oldStatus != updatedContract.getStatus()) {
            if (updatedContract.getStatus() == ContractStatus.ACTIVE) {
                contractEventProducer.publishContractActivated(updatedContract);
            } else if (updatedContract.getStatus() == ContractStatus.TERMINATED) {
                contractEventProducer.publishContractTerminated(updatedContract);
            }
        }
        
        return updatedContract;
    }

    @Override
    public void delete(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));
        
        // Récupérer tous les tenants associés pour libérer les chambres
        List<Tenant> tenants = tenantRepository.findAll().stream()
                .filter(t -> t.getContractId().equals(id))
                .toList();
        
        contractRepository.deleteById(id);
        
        // Publier l'événement de résiliation pour chaque tenant (pour libérer les chambres)
        for (Tenant tenant : tenants) {
            if (tenant.getRoomId() != null) {
                contractEventProducer.publishContractTerminated(contract, tenant.getRoomId());
            }
        }
    }
}






