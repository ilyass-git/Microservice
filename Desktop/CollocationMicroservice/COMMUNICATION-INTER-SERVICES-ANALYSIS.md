# 📡 Analyse des Communications Inter-Services

## ✅ Communications Existantes (OpenFeign)

### 1. **Contrat Service → Utilisateur Service**
- **Client Feign** : `UserServiceClient`
- **Endpoint utilisé** : `GET /api/users/{id}`
- **Usage** : 
  - `TenantServiceImpl.create()` : Vérifie que l'utilisateur existe avant de créer un tenant
- **Fichier** : `contrat-service/.../client/UserServiceClient.java`

### 2. **Annonce Service → Utilisateur Service**
- **Client Feign** : `UserServiceClient`
- **Endpoint utilisé** : `GET /api/users/{id}`
- **Usage** :
  - `AdServiceImpl.create()` : Vérifie que le propriétaire existe avant de créer une annonce
- **Fichier** : `annonce-service/.../client/UserServiceClient.java`

---

## ❌ Communications Manquantes (À Ajouter)

### 🔴 **PRIORITÉ HAUTE - Synchrones (OpenFeign)**

#### 1. **Contrat Service → Annonce Service**

**Pourquoi ?**
- `Contract` a un `propertyId` → besoin de vérifier que la propriété existe
- `Tenant` a un `roomId` → besoin de vérifier que la chambre existe et est disponible

**Clients Feign à créer :**

```java
// contrat-service/.../client/PropertyServiceClient.java
@FeignClient(name = "annonce-service", path = "/api/properties")
public interface PropertyServiceClient {
    @GetMapping("/{id}")
    ResponseEntity<PropertyDto> getPropertyById(@PathVariable Long id);
    
    @GetMapping("/{id}/available")
    ResponseEntity<Boolean> isPropertyAvailable(@PathVariable Long id);
}
```

```java
// contrat-service/.../client/RoomServiceClient.java
@FeignClient(name = "annonce-service", path = "/api/rooms")
public interface RoomServiceClient {
    @GetMapping("/{id}")
    ResponseEntity<RoomDto> getRoomById(@PathVariable Long id);
    
    @GetMapping("/{id}/available")
    ResponseEntity<Boolean> isRoomAvailable(@PathVariable Long id);
    
    @PutMapping("/{id}/availability")
    ResponseEntity<Void> updateAvailability(@PathVariable Long id, @RequestBody Boolean isAvailable);
}
```

**Où utiliser :**
- `ContractServiceImpl.create()` : Vérifier que `propertyId` existe
- `TenantServiceImpl.create()` : Vérifier que `roomId` existe et est disponible, puis marquer comme non disponible

---

#### 2. **Annonce Service → Contrat Service** (Optionnel)

**Pourquoi ?**
- Vérifier si une propriété/chambre a des contrats actifs avant de la supprimer
- Obtenir les statistiques de location

**Client Feign à créer :**

```java
// annonce-service/.../client/ContractServiceClient.java
@FeignClient(name = "contrat-service", path = "/api/contracts")
public interface ContractServiceClient {
    @GetMapping("/property/{propertyId}")
    ResponseEntity<List<ContractDto>> getContractsByPropertyId(@PathVariable Long propertyId);
    
    @GetMapping("/property/{propertyId}/active")
    ResponseEntity<Boolean> hasActiveContracts(@PathVariable Long propertyId);
}
```

---

### 🟡 **PRIORITÉ MOYENNE - Asynchrones (Kafka)**

#### 3. **Événements de Notification**

**Pourquoi utiliser Kafka ?**
- Découplage des services
- Notifications en temps réel
- Scalabilité
- Résilience (retry automatique)

**Topics Kafka à créer :**

##### Topic 1 : `user-events`
```json
{
  "eventType": "USER_CREATED | USER_UPDATED | USER_DELETED",
  "userId": 1,
  "timestamp": "2025-11-25T10:00:00Z",
  "data": { ... }
}
```

**Consommateurs :**
- `annonce-service` : Mettre à jour les références si l'utilisateur est supprimé
- `contrat-service` : Mettre à jour les références si l'utilisateur est supprimé

##### Topic 2 : `contract-events`
```json
{
  "eventType": "CONTRACT_CREATED | CONTRACT_ACTIVATED | CONTRACT_TERMINATED | PAYMENT_RECEIVED",
  "contractId": 1,
  "propertyId": 1,
  "roomId": 1,
  "timestamp": "2025-11-25T10:00:00Z",
  "data": { ... }
}
```

**Consommateurs :**
- `annonce-service` : 
  - Marquer la chambre comme non disponible quand un contrat est créé
  - Marquer la chambre comme disponible quand un contrat est terminé
  - Mettre à jour le statut de l'annonce

##### Topic 3 : `property-events`
```json
{
  "eventType": "PROPERTY_CREATED | PROPERTY_UPDATED | PROPERTY_DELETED | ROOM_AVAILABILITY_CHANGED",
  "propertyId": 1,
  "roomId": 1,
  "timestamp": "2025-11-25T10:00:00Z",
  "data": { ... }
}
```

**Consommateurs :**
- `contrat-service` : 
  - Invalider les contrats si la propriété est supprimée
  - Notifier les locataires si la chambre devient indisponible

##### Topic 4 : `ad-events`
```json
{
  "eventType": "AD_PUBLISHED | AD_CLOSED",
  "adId": 1,
  "propertyId": 1,
  "roomId": 1,
  "timestamp": "2025-11-25T10:00:00Z",
  "data": { ... }
}
```

**Consommateurs :**
- `contrat-service` : Synchroniser les informations d'annonce avec les contrats

---

### 🟢 **PRIORITÉ BASSE - Améliorations Futures**

#### 4. **Service de Recherche/Recommandation**

**Communication suggérée :**
- Service dédié qui agrège les données de tous les services
- Utilise OpenFeign pour récupérer les données
- Cache les résultats pour améliorer les performances

#### 5. **Service de Notification**

**Communication suggérée :**
- Kafka pour recevoir les événements
- Envoi d'emails/SMS aux utilisateurs
- Notifications push

---

## 📊 Matrice des Communications

| Service Source | Service Cible | Type | Technologie | Statut | Priorité |
|---------------|--------------|------|-------------|--------|----------|
| Contrat Service | Utilisateur Service | Synchrone | OpenFeign | ✅ Existant | - |
| Annonce Service | Utilisateur Service | Synchrone | OpenFeign | ✅ Existant | - |
| Contrat Service | Annonce Service | Synchrone | OpenFeign | ❌ Manquant | 🔴 HAUTE |
| Annonce Service | Contrat Service | Synchrone | OpenFeign | ❌ Manquant | 🟡 MOYENNE |
| Tous Services | Tous Services | Asynchrone | Kafka | ❌ Manquant | 🟡 MOYENNE |

---

## 🎯 Recommandations d'Implémentation

### Phase 1 : Communications Synchrones Critiques (OpenFeign)

1. **Créer `PropertyServiceClient` dans contrat-service**
   - Vérifier l'existence de la propriété avant de créer un contrat
   - Implémenter dans `ContractServiceImpl.create()`

2. **Créer `RoomServiceClient` dans contrat-service**
   - Vérifier l'existence et la disponibilité de la chambre
   - Marquer la chambre comme non disponible lors de la création d'un tenant
   - Implémenter dans `TenantServiceImpl.create()`

### Phase 2 : Événements Asynchrones (Kafka)

1. **Configurer Kafka**
   - Ajouter les dépendances Spring Kafka
   - Configurer les brokers
   - Créer les topics

2. **Implémenter les Producteurs**
   - `user-events` : Dans utilisateur-service
   - `contract-events` : Dans contrat-service
   - `property-events` : Dans annonce-service
   - `ad-events` : Dans annonce-service

3. **Implémenter les Consommateurs**
   - Mettre à jour les disponibilités des chambres
   - Synchroniser les données entre services
   - Gérer les événements de suppression

### Phase 3 : Optimisations

1. **Circuit Breaker** (Resilience4j)
   - Gérer les pannes des services
   - Fallback automatique

2. **Cache** (Redis)
   - Mettre en cache les appels fréquents
   - Réduire la charge sur les services

3. **API Gateway**
   - Centraliser les appels
   - Rate limiting
   - Authentification

---

## 📝 Exemple d'Implémentation

### Exemple 1 : PropertyServiceClient

```java
// contrat-service/.../client/PropertyServiceClient.java
@FeignClient(name = "annonce-service", path = "/api/properties")
public interface PropertyServiceClient {
    @GetMapping("/{id}")
    ResponseEntity<PropertyDto> getPropertyById(@PathVariable Long id);
}
```

```java
// contrat-service/.../service/impl/ContractServiceImpl.java
@Service
@RequiredArgsConstructor
@Slf4j
public class ContractServiceImpl implements IContractService {
    private final ContractRepository contractRepository;
    private final PropertyServiceClient propertyServiceClient; // Nouveau
    
    @Override
    public Contract create(Contract contract) {
        // Vérifier que la propriété existe
        log.info("🔗 [COMMUNICATION] Vérification de la propriété ID: {}", contract.getPropertyId());
        ResponseEntity<PropertyDto> response = propertyServiceClient.getPropertyById(contract.getPropertyId());
        
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Propriété avec ID " + contract.getPropertyId() + " n'existe pas");
        }
        
        log.info("✅ Propriété trouvée: {}", response.getBody().getTitle());
        return contractRepository.save(contract);
    }
}
```

### Exemple 2 : Producer Kafka

```java
// contrat-service/.../config/KafkaProducer.java
@Service
@RequiredArgsConstructor
@Slf4j
public class ContractEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void publishContractCreated(Contract contract) {
        ContractEvent event = ContractEvent.builder()
            .eventType("CONTRACT_CREATED")
            .contractId(contract.getId())
            .propertyId(contract.getPropertyId())
            .timestamp(LocalDateTime.now())
            .build();
            
        kafkaTemplate.send("contract-events", event);
        log.info("📤 [KAFKA] Événement publié: CONTRACT_CREATED pour contrat ID: {}", contract.getId());
    }
}
```

### Exemple 3 : Consumer Kafka

```java
// annonce-service/.../config/KafkaConsumer.java
@Component
@RequiredArgsConstructor
@Slf4j
public class ContractEventConsumer {
    private final RoomRepository roomRepository;
    
    @KafkaListener(topics = "contract-events", groupId = "annonce-service")
    public void handleContractEvent(ContractEvent event) {
        log.info("📥 [KAFKA] Événement reçu: {} pour contrat ID: {}", event.getEventType(), event.getContractId());
        
        if ("CONTRACT_CREATED".equals(event.getEventType()) && event.getRoomId() != null) {
            // Marquer la chambre comme non disponible
            roomRepository.findById(event.getRoomId()).ifPresent(room -> {
                room.setIsAvailable(false);
                roomRepository.save(room);
                log.info("✅ Chambre ID {} marquée comme non disponible", event.getRoomId());
            });
        }
    }
}
```

---

## 🔧 Configuration Kafka (À Ajouter)

### pom.xml (pour chaque service)
```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

### application.properties
```properties
# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=${spring.application.name}
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
```

---

## 📊 Résumé

### Communications Existantes : 2
- ✅ Contrat Service → Utilisateur Service
- ✅ Annonce Service → Utilisateur Service

### Communications à Ajouter : 5+
- 🔴 Contrat Service → Annonce Service (Property + Room) - **CRITIQUE**
- 🟡 Annonce Service → Contrat Service - **RECOMMANDÉ**
- 🟡 Événements Kafka (4 topics) - **RECOMMANDÉ**
- 🟢 Service de Recherche - **FUTUR**
- 🟢 Service de Notification - **FUTUR**

---

**Prochaine étape recommandée** : Implémenter `PropertyServiceClient` et `RoomServiceClient` dans contrat-service pour valider les propriétés et chambres avant de créer des contrats.

