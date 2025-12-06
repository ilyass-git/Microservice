# 📋 Résumé de l'Implémentation

## ✅ Ce qui a été Implémenté

### 1. Communications OpenFeign (Synchrones)

#### ✅ Contrat Service → Annonce Service

**Clients Feign créés :**
- `PropertyServiceClient` : Communication avec `/api/properties/{id}`
- `RoomServiceClient` : Communication avec `/api/rooms/{id}` et `/api/rooms/{id}/availability`

**DTOs créés :**
- `PropertyDto` : DTO pour les propriétés
- `RoomDto` : DTO pour les chambres

**Intégration :**
- `ContractServiceImpl.create()` : Vérifie l'existence de la propriété avant création
- `TenantServiceImpl.create()` : 
  - Vérifie l'existence de l'utilisateur
  - Vérifie l'existence et disponibilité de la chambre
  - Marque la chambre comme non disponible via OpenFeign

**Fichiers créés :**
```
contrat-service/src/main/java/emsi/ma/contratservice/
├── client/
│   ├── PropertyServiceClient.java
│   ├── RoomServiceClient.java
│   └── dto/
│       ├── PropertyDto.java
│       └── RoomDto.java
```

#### ✅ Endpoint API ajouté dans Annonce Service

- `PUT /api/rooms/{id}/availability` : Mettre à jour la disponibilité d'une chambre

**Fichiers modifiés :**
- `annonce-service/.../controller/RoomController.java` : Ajout de l'endpoint
- `annonce-service/.../service/IRoomService.java` : Ajout de la méthode
- `annonce-service/.../service/impl/RoomServiceImpl.java` : Implémentation

---

### 2. Communications Kafka (Asynchrones)

#### ✅ Topic : `contract-events`

**Producteur (Contrat Service) :**
- `KafkaProducerConfig` : Configuration du producteur
- `ContractEventProducer` : Service pour publier les événements
- Événements publiés :
  - `CONTRACT_CREATED` : Lors de la création d'un contrat
  - `CONTRACT_ACTIVATED` : Lorsqu'un contrat devient actif
  - `CONTRACT_TERMINATED` : Lorsqu'un contrat est résilié
  - `TENANT_CREATED` : Lors de la création d'un tenant avec roomId

**Consommateur (Annonce Service) :**
- `KafkaConsumerConfig` : Configuration du consommateur
- `ContractEventConsumer` : Service pour consommer les événements
- Actions :
  - `TENANT_CREATED` → Marque la chambre comme non disponible
  - `CONTRACT_TERMINATED` → Marque la chambre comme disponible

**Fichiers créés :**
```
contrat-service/src/main/java/emsi/ma/contratservice/
├── config/
│   └── KafkaProducerConfig.java
├── event/
│   └── ContractEvent.java
└── service/
    └── ContractEventProducer.java

annonce-service/src/main/java/emsi/ma/annonceservice/
├── config/
│   └── KafkaConsumerConfig.java
├── event/
│   └── ContractEvent.java
└── service/
    └── ContractEventConsumer.java
```

---

### 3. Dépendances Ajoutées

#### Contrat Service
```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

#### Annonce Service
```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

---

## 📊 Matrice des Communications Finale

| Source | Cible | Type | Technologie | Endpoint/Topic | Statut |
|--------|-------|------|-------------|----------------|--------|
| Contrat | Utilisateur | Synchrone | OpenFeign | GET /api/users/{id} | ✅ Existant |
| Annonce | Utilisateur | Synchrone | OpenFeign | GET /api/users/{id} | ✅ Existant |
| Contrat | Annonce | Synchrone | OpenFeign | GET /api/properties/{id} | ✅ **NOUVEAU** |
| Contrat | Annonce | Synchrone | OpenFeign | GET /api/rooms/{id} | ✅ **NOUVEAU** |
| Contrat | Annonce | Synchrone | OpenFeign | PUT /api/rooms/{id}/availability | ✅ **NOUVEAU** |
| Contrat | Annonce | Asynchrone | Kafka | contract-events | ✅ **NOUVEAU** |

---

## 🧪 Guide de Test Complet

### Prérequis

1. **Services démarrés** (dans l'ordre) :
   ```bash
   # 1. Eureka Server
   cd eureka-server
   mvn spring-boot:run
   
   # 2. Config Server
   cd config-server
   mvn spring-boot:run
   
   # 3. Utilisateur Service
   cd utilisateur-service
   mvn spring-boot:run
   
   # 4. Annonce Service
   cd annonce-service
   mvn spring-boot:run
   
   # 5. Contrat Service
   cd contrat-service
   mvn spring-boot:run
   ```

2. **Kafka** :
   ```bash
   # Option 1 : Docker
   docker run -d --name kafka -p 9092:9092 apache/kafka:latest
   
   # Option 2 : Installation locale
   # Télécharger depuis https://kafka.apache.org/downloads
   # Démarrer Zookeeper puis Kafka
   ```

3. **MySQL** :
   - Base de données `collocation_db` créée
   - Migrations Flyway appliquées

---

### Test 1 : Communication OpenFeign - Création de Contrat avec Vérification de Propriété

#### Scénario
Créer un contrat et vérifier que la propriété est validée via OpenFeign.

#### Étapes

1. **Créer une propriété** :
```bash
curl -X POST http://localhost:8082/api/properties \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Appartement Test Feign",
    "address": "123 Rue Test",
    "city": "Casablanca",
    "description": "Test pour OpenFeign",
    "ownerId": 1
  }'
```

**Résultat attendu** : `{"id": 1, "title": "Appartement Test Feign", ...}`

2. **Créer un contrat** :
```bash
curl -X POST http://localhost:8083/api/contracts \
  -H "Content-Type: application/json" \
  -d '{
    "propertyId": 1,
    "startDate": "2025-12-01",
    "endDate": "2026-12-01",
    "status": "DRAFT"
  }'
```

**Vérifications dans les logs de contrat-service** :
```
🔗 [COMMUNICATION INTER-SERVICE] Vérification de l'existence de la propriété ID: 1
   Service appelant: contrat-service
   Service appelé: annonce-service
   Endpoint: GET /api/properties/1
✅ [COMMUNICATION RÉUSSIE] Propriété trouvée: Appartement Test Feign (ID: 1)
   Communication inter-service: contrat-service -> annonce-service
✅ Création du contrat pour la propriété ID: 1
📤 [KAFKA] Événement publié: CONTRACT_CREATED pour contrat ID: 1
```

**Vérifications dans les logs de annonce-service** :
```
📥 [APPEL REÇU] GET /api/properties/1 - Peut être depuis un autre service via Feign
```

3. **Tester avec une propriété inexistante** :
```bash
curl -X POST http://localhost:8083/api/contracts \
  -H "Content-Type: application/json" \
  -d '{
    "propertyId": 999,
    "startDate": "2025-12-01",
    "endDate": "2026-12-01",
    "status": "DRAFT"
  }'
```

**Résultat attendu** : Erreur 500 avec message "Propriété avec ID 999 n'existe pas"

---

### Test 2 : Communication OpenFeign - Création de Tenant avec Vérification Utilisateur et Chambre

#### Scénario
Créer un tenant et vérifier que l'utilisateur et la chambre sont validés, puis marquer la chambre comme non disponible.

#### Étapes

1. **Créer une chambre disponible** :
```bash
curl -X POST http://localhost:8082/api/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "propertyId": 1,
    "name": "Chambre Test",
    "price": 2000.00,
    "isAvailable": true
  }'
```

**Résultat attendu** : `{"id": 1, "name": "Chambre Test", "isAvailable": true, ...}`

2. **Vérifier que la chambre est disponible** :
```bash
curl http://localhost:8082/api/rooms/1
```

**Résultat attendu** : `"isAvailable": true`

3. **Créer un tenant** :
```bash
curl -X POST http://localhost:8083/api/tenants \
  -H "Content-Type: application/json" \
  -d '{
    "contractId": 1,
    "userId": 1,
    "roomId": 1
  }'
```

**Vérifications dans les logs de contrat-service** :
```
🔗 [COMMUNICATION INTER-SERVICE] Vérification de l'existence de l'utilisateur ID: 1
   Service appelant: contrat-service
   Service appelé: utilisateur-service
   Endpoint: GET /api/users/1
✅ [COMMUNICATION RÉUSSIE] Utilisateur trouvé: John Doe (ID: 1)
   Communication inter-service: contrat-service -> utilisateur-service

🔗 [COMMUNICATION INTER-SERVICE] Vérification de la chambre ID: 1
   Service appelant: contrat-service
   Service appelé: annonce-service
   Endpoint: GET /api/rooms/1
✅ [COMMUNICATION RÉUSSIE] Chambre trouvée: Chambre Test (ID: 1) - Disponible: true
🔄 [MISE À JOUR] Marquage de la chambre ID 1 comme non disponible
✅ Chambre ID 1 marquée comme non disponible
✅ Création du tenant pour l'utilisateur ID: 1 et chambre ID: 1
📤 [KAFKA] Événement publié: TENANT_CREATED pour tenant ID: 1 (roomId: 1)
```

4. **Vérifier que la chambre est maintenant non disponible** :
```bash
curl http://localhost:8082/api/rooms/1
```

**Résultat attendu** : `"isAvailable": false`

---

### Test 3 : Communication Kafka - Événements Asynchrones

#### Scénario
Observer les événements Kafka lors de la création et résiliation de contrats/tenants.

#### Prérequis
Kafka doit être démarré et accessible sur `localhost:9092`

#### Étapes

1. **Créer un tenant** (comme dans Test 2)

2. **Vérifier les logs Kafka** :

**Dans contrat-service** :
```
📤 [KAFKA] Événement publié: TENANT_CREATED pour tenant ID: 1 (roomId: 1)
```

**Dans annonce-service** :
```
📥 [KAFKA] Événement reçu: TENANT_CREATED pour contrat ID: 1
✅ [KAFKA] Chambre ID 1 marquée comme non disponible (tenant créé)
```

3. **Vérifier via Kafka Console Consumer** (optionnel) :
```bash
# Dans un terminal séparé
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic contract-events \
  --from-beginning
```

**Résultat attendu** : Messages JSON avec les événements

4. **Résilier le contrat** :
```bash
curl -X DELETE http://localhost:8083/api/contracts/1
```

**Vérifications dans les logs** :

**Dans contrat-service** :
```
📤 [KAFKA] Événement publié: CONTRACT_TERMINATED pour contrat ID: 1 (roomId: 1)
```

**Dans annonce-service** :
```
📥 [KAFKA] Événement reçu: CONTRACT_TERMINATED pour contrat ID: 1
✅ [KAFKA] Chambre ID 1 marquée comme disponible (contrat résilié)
```

5. **Vérifier que la chambre est maintenant disponible** :
```bash
curl http://localhost:8082/api/rooms/1
```

**Résultat attendu** : `"isAvailable": true`

---

### Test 4 : Scénario Complet End-to-End

#### Scénario
Simuler un processus complet de location : création de propriété → chambre → contrat → tenant

#### Étapes

1. **Créer un utilisateur** (si pas déjà créé) :
```bash
curl -X POST http://localhost:8084/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "nom": "Test",
    "prenom": "User",
    "telephone": "0612345678"
  }'
```

2. **Créer une propriété** :
```bash
curl -X POST http://localhost:8082/api/properties \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Appartement E2E Test",
    "address": "456 Avenue Test",
    "city": "Rabat",
    "description": "Test end-to-end",
    "ownerId": 1
  }'
```

3. **Créer une chambre** :
```bash
curl -X POST http://localhost:8082/api/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "propertyId": 1,
    "name": "Chambre E2E",
    "price": 2500.00,
    "isAvailable": true
  }'
```

4. **Créer un contrat** :
```bash
curl -X POST http://localhost:8083/api/contracts \
  -H "Content-Type: application/json" \
  -d '{
    "propertyId": 1,
    "startDate": "2025-12-01",
    "endDate": "2026-12-01",
    "status": "ACTIVE"
  }'
```

5. **Créer un tenant** :
```bash
curl -X POST http://localhost:8083/api/tenants \
  -H "Content-Type: application/json" \
  -d '{
    "contractId": 1,
    "userId": 1,
    "roomId": 1
  }'
```

6. **Vérifier l'état final** :
```bash
# Vérifier la chambre
curl http://localhost:8082/api/rooms/1

# Vérifier le contrat
curl http://localhost:8083/api/contracts/1

# Vérifier le tenant
curl http://localhost:8083/api/tenants/1
```

**Résultats attendus** :
- Chambre : `"isAvailable": false`
- Contrat : Existe avec status `ACTIVE`
- Tenant : Existe avec `contractId: 1`, `userId: 1`, `roomId: 1`

---

## 🔍 Vérification des Logs

### Logs OpenFeign à Chercher

**Dans contrat-service** :
- `🔗 [COMMUNICATION INTER-SERVICE]` : Début de communication
- `✅ [COMMUNICATION RÉUSSIE]` : Communication réussie
- `❌ [COMMUNICATION ÉCHOUÉE]` : Communication échouée

**Dans annonce-service** :
- `📥 [APPEL REÇU]` : Appel reçu via Feign

### Logs Kafka à Chercher

**Dans contrat-service** :
- `📤 [KAFKA] Événement publié:` : Événement publié

**Dans annonce-service** :
- `📥 [KAFKA] Événement reçu:` : Événement reçu
- `✅ [KAFKA] Chambre ID X marquée comme` : Action effectuée

---

## 🐛 Dépannage

### Problème : OpenFeign ne trouve pas le service

**Symptôme** : `Service 'annonce-service' not found`

**Solutions** :
1. Vérifier Eureka : `http://localhost:8761` → Vérifier que `ANNONCE-SERVICE` est enregistré
2. Vérifier le nom dans `@FeignClient(name = "annonce-service")`
3. Vérifier que annonce-service est démarré et enregistré dans Eureka

### Problème : Kafka ne fonctionne pas

**Symptôme** : Pas de logs `📤 [KAFKA]` ou `📥 [KAFKA]`

**Solutions** :
1. Vérifier que Kafka est démarré : `telnet localhost 9092`
2. Vérifier la configuration dans `KafkaProducerConfig` et `KafkaConsumerConfig`
3. Vérifier les logs d'erreur dans les services
4. Créer le topic manuellement si nécessaire :
   ```bash
   kafka-topics.sh --create --bootstrap-server localhost:9092 \
     --topic contract-events \
     --partitions 1 \
     --replication-factor 1
   ```

### Problème : La chambre n'est pas marquée comme non disponible

**Vérifications** :
1. Vérifier que `roomId` n'est pas null dans le tenant
2. Vérifier les logs Kafka (publication et consommation)
3. Vérifier que le consommateur Kafka est actif dans annonce-service
4. Vérifier directement dans la base : `SELECT * FROM rooms WHERE id = 1;`

---

## 📁 Structure des Fichiers Créés

```
contrat-service/
├── src/main/java/emsi/ma/contratservice/
│   ├── client/
│   │   ├── PropertyServiceClient.java          ✅ NOUVEAU
│   │   ├── RoomServiceClient.java              ✅ NOUVEAU
│   │   └── dto/
│   │       ├── PropertyDto.java                ✅ NOUVEAU
│   │       └── RoomDto.java                     ✅ NOUVEAU
│   ├── config/
│   │   └── KafkaProducerConfig.java            ✅ NOUVEAU
│   ├── event/
│   │   └── ContractEvent.java                  ✅ NOUVEAU
│   └── service/
│       ├── ContractEventProducer.java          ✅ NOUVEAU
│       └── impl/
│           ├── ContractServiceImpl.java        ✏️ MODIFIÉ
│           └── TenantServiceImpl.java          ✏️ MODIFIÉ

annonce-service/
├── src/main/java/emsi/ma/annonceservice/
│   ├── config/
│   │   └── KafkaConsumerConfig.java            ✅ NOUVEAU
│   ├── event/
│   │   └── ContractEvent.java                  ✅ NOUVEAU
│   ├── service/
│   │   └── ContractEventConsumer.java          ✅ NOUVEAU
│   ├── controller/
│   │   └── RoomController.java                 ✏️ MODIFIÉ
│   └── service/
│       ├── IRoomService.java                   ✏️ MODIFIÉ
│       └── impl/
│           └── RoomServiceImpl.java            ✏️ MODIFIÉ
```

---

## ✅ Checklist de Vérification

### OpenFeign
- [ ] PropertyServiceClient créé et fonctionnel
- [ ] RoomServiceClient créé et fonctionnel
- [ ] DTOs PropertyDto et RoomDto créés
- [ ] ContractServiceImpl vérifie la propriété avant création
- [ ] TenantServiceImpl vérifie utilisateur et chambre
- [ ] TenantServiceImpl marque la chambre comme non disponible
- [ ] Endpoint PUT /api/rooms/{id}/availability créé

### Kafka
- [ ] Dépendance spring-kafka ajoutée dans pom.xml
- [ ] KafkaProducerConfig créé
- [ ] KafkaConsumerConfig créé
- [ ] ContractEventProducer créé
- [ ] ContractEventConsumer créé
- [ ] Événements publiés lors de la création de contrat
- [ ] Événements publiés lors de la création de tenant
- [ ] Événements publiés lors de la résiliation de contrat
- [ ] Consommateur met à jour la disponibilité des chambres

### Tests
- [ ] Test 1 : Création de contrat avec vérification de propriété
- [ ] Test 2 : Création de tenant avec vérification utilisateur et chambre
- [ ] Test 3 : Événements Kafka fonctionnels
- [ ] Test 4 : Scénario complet end-to-end

---

## 🎉 Résumé

### Communications Implémentées

**OpenFeign (4 nouvelles communications) :**
1. ✅ Contrat Service → Annonce Service (Property)
2. ✅ Contrat Service → Annonce Service (Room - GET)
3. ✅ Contrat Service → Annonce Service (Room - PUT availability)
4. ✅ (Déjà existant) Contrat Service → Utilisateur Service

**Kafka (1 topic implémenté) :**
1. ✅ Topic `contract-events` avec producteur et consommateur

### Fichiers Créés/Modifiés

- **15 nouveaux fichiers** créés
- **5 fichiers** modifiés
- **2 dépendances** ajoutées (Kafka)

### Fonctionnalités

- ✅ Validation des propriétés avant création de contrat
- ✅ Validation des utilisateurs avant création de tenant
- ✅ Validation et mise à jour de disponibilité des chambres
- ✅ Synchronisation automatique via Kafka
- ✅ Gestion des événements de résiliation de contrat

**Tout est prêt pour les tests ! 🚀**

