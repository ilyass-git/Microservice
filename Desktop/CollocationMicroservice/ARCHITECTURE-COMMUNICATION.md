# 🏗️ Architecture des Communications Inter-Services

## 📋 Table des Matières

1. [Vue d'Ensemble](#vue-densemble)
2. [Communications Synchrones (OpenFeign)](#communications-synchrones-openfeign)
3. [Communications Asynchrones (Kafka)](#communications-asynchrones-kafka)
4. [Architecture Complète](#architecture-complète)
5. [Ce qui a été Implémenté](#ce-qui-a-été-implémenté)
6. [Comment Tester](#comment-tester)

---

## 🎯 Vue d'Ensemble

Ce projet utilise deux types de communications inter-services :

1. **OpenFeign** : Communications synchrones pour les validations et récupérations de données
2. **Kafka** : Communications asynchrones pour les événements et notifications

---

## 🔄 Communications Synchrones (OpenFeign)

### Architecture OpenFeign

```
┌─────────────────┐
│  Eureka Server  │ ← Service Discovery
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌─────────┐ ┌─────────┐
│ Contrat │ │ Annonce │
│ Service │ │ Service │
└────┬────┘ └────┬────┘
     │           │
     │           │
     └─────┬─────┘
           │
           ▼
    ┌─────────┐
    │Utilisateur│
    │ Service  │
    └──────────┘
```

### Communications Implémentées

#### 1. **Contrat Service → Utilisateur Service**
- **Client** : `UserServiceClient`
- **Endpoint** : `GET /api/users/{id}`
- **Usage** : Vérification de l'existence de l'utilisateur avant création d'un tenant
- **Fichier** : `contrat-service/.../client/UserServiceClient.java`

#### 2. **Annonce Service → Utilisateur Service**
- **Client** : `UserServiceClient`
- **Endpoint** : `GET /api/users/{id}`
- **Usage** : Vérification de l'existence du propriétaire avant création d'une annonce
- **Fichier** : `annonce-service/.../client/UserServiceClient.java`

#### 3. **Contrat Service → Annonce Service (Property)**
- **Client** : `PropertyServiceClient`
- **Endpoint** : `GET /api/properties/{id}`
- **Usage** : Vérification de l'existence de la propriété avant création d'un contrat
- **Fichier** : `contrat-service/.../client/PropertyServiceClient.java`

#### 4. **Contrat Service → Annonce Service (Room)**
- **Client** : `RoomServiceClient`
- **Endpoints** : 
  - `GET /api/rooms/{id}` : Récupérer une chambre
  - `PUT /api/rooms/{id}/availability` : Mettre à jour la disponibilité
- **Usage** : 
  - Vérification de l'existence et disponibilité de la chambre avant création d'un tenant
  - Marquage de la chambre comme non disponible lors de la création d'un tenant
- **Fichier** : `contrat-service/.../client/RoomServiceClient.java`

---

## 📨 Communications Asynchrones (Kafka)

### Architecture Kafka

```
┌─────────────────┐
│  Kafka Broker   │
│  (localhost:    │
│    9092)        │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌─────────┐ ┌─────────┐
│ Contrat │ │ Annonce │
│ Service │ │ Service │
│(Producer)│ │(Consumer)│
└─────────┘ └─────────┘
```

### Topic Kafka Implémenté

#### **Topic : `contract-events`**

**Producteur** : `contrat-service`
- **Classe** : `ContractEventProducer`
- **Événements publiés** :
  - `CONTRACT_CREATED` : Quand un contrat est créé
  - `CONTRACT_ACTIVATED` : Quand un contrat devient actif
  - `CONTRACT_TERMINATED` : Quand un contrat est résilié
  - `TENANT_CREATED` : Quand un tenant est créé (avec roomId)

**Consommateur** : `annonce-service`
- **Classe** : `ContractEventConsumer`
- **Actions** :
  - `TENANT_CREATED` → Marque la chambre comme non disponible
  - `CONTRACT_TERMINATED` → Marque la chambre comme disponible

**Structure de l'événement** :
```json
{
  "eventType": "CONTRACT_CREATED | CONTRACT_ACTIVATED | CONTRACT_TERMINATED | TENANT_CREATED",
  "contractId": 1,
  "propertyId": 1,
  "roomId": 1,
  "timestamp": "2025-11-25T10:00:00",
  "data": null
}
```

---

## 🏛️ Architecture Complète

```
┌─────────────────────────────────────────────────────────────┐
│                    Eureka Server                            │
│                  (Service Discovery)                        │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│   Contrat    │   │   Annonce    │   │ Utilisateur  │
│   Service    │   │   Service    │   │   Service    │
│              │   │              │   │              │
│ OpenFeign:   │   │ OpenFeign:   │   │              │
│ - UserClient │   │ - UserClient │   │              │
│ - Property   │   │              │   │              │
│   Client     │   │              │   │              │
│ - RoomClient │   │              │   │              │
│              │   │              │   │              │
│ Kafka:       │   │ Kafka:       │   │              │
│ - Producer   │   │ - Consumer   │   │              │
│   (contract- │   │   (contract- │   │              │
│    events)   │   │    events)    │   │              │
└──────────────┘   └──────────────┘   └──────────────┘
        │                   │
        └───────────────────┘
                    │
                    ▼
            ┌──────────────┐
            │ Kafka Broker │
            │  (localhost: │
            │    9092)     │
            └──────────────┘
```

---

## ✅ Ce qui a été Implémenté

### 1. Clients OpenFeign

#### Contrat Service
- ✅ `UserServiceClient` : Communication avec utilisateur-service
- ✅ `PropertyServiceClient` : Communication avec annonce-service (propriétés)
- ✅ `RoomServiceClient` : Communication avec annonce-service (chambres)

#### Annonce Service
- ✅ `UserServiceClient` : Communication avec utilisateur-service

### 2. DTOs Partagés

#### Contrat Service
- ✅ `UserResponseDto` : DTO pour les utilisateurs
- ✅ `PropertyDto` : DTO pour les propriétés
- ✅ `RoomDto` : DTO pour les chambres

### 3. Intégration dans les Services

#### ContractServiceImpl
- ✅ Vérification de l'existence de la propriété avant création
- ✅ Publication d'événements Kafka (CONTRACT_CREATED, CONTRACT_ACTIVATED, CONTRACT_TERMINATED)

#### TenantServiceImpl
- ✅ Vérification de l'existence de l'utilisateur avant création
- ✅ Vérification de l'existence et disponibilité de la chambre
- ✅ Marquage de la chambre comme non disponible via OpenFeign
- ✅ Publication d'événement Kafka (TENANT_CREATED)

### 4. Configuration Kafka

#### Contrat Service (Producteur)
- ✅ `KafkaProducerConfig` : Configuration du producteur
- ✅ `ContractEventProducer` : Service pour publier les événements

#### Annonce Service (Consommateur)
- ✅ `KafkaConsumerConfig` : Configuration du consommateur
- ✅ `ContractEventConsumer` : Service pour consommer les événements
- ✅ Mise à jour automatique de la disponibilité des chambres

### 5. Endpoints API Ajoutés

#### Annonce Service
- ✅ `PUT /api/rooms/{id}/availability` : Mettre à jour la disponibilité d'une chambre

---

## 🧪 Comment Tester

### Prérequis

1. **Services démarrés** :
   - Eureka Server (port 8761)
   - Config Server (port 8888)
   - Utilisateur Service (port 8084)
   - Annonce Service (port 8082)
   - Contrat Service (port 8083)

2. **Kafka** :
   - Kafka Broker démarré (localhost:9092)
   - Zookeeper démarré (si nécessaire)

3. **Base de données** :
   - MySQL démarré
   - Base `collocation_db` créée

### Test 1 : Communication OpenFeign - Création de Contrat

#### Étape 1 : Créer une propriété
```bash
POST http://localhost:8082/api/properties
Content-Type: application/json

{
  "title": "Appartement Test",
  "address": "123 Rue Test",
  "city": "Casablanca",
  "description": "Test property",
  "ownerId": 1
}
```

**Résultat attendu** : Propriété créée avec ID (ex: 1)

#### Étape 2 : Créer un contrat
```bash
POST http://localhost:8083/api/contracts
Content-Type: application/json

{
  "propertyId": 1,
  "startDate": "2025-12-01",
  "endDate": "2026-12-01",
  "status": "DRAFT"
}
```

**Vérifications** :
- ✅ Logs dans contrat-service : "🔗 [COMMUNICATION INTER-SERVICE] Vérification de la propriété ID: 1"
- ✅ Logs dans contrat-service : "✅ [COMMUNICATION RÉUSSIE] Propriété trouvée"
- ✅ Contrat créé avec succès

**Si la propriété n'existe pas** :
- ❌ Erreur : "Propriété avec ID X n'existe pas"

---

### Test 2 : Communication OpenFeign - Création de Tenant

#### Étape 1 : Créer une chambre disponible
```bash
POST http://localhost:8082/api/rooms
Content-Type: application/json

{
  "propertyId": 1,
  "name": "Chambre 1",
  "price": 2000.00,
  "isAvailable": true
}
```

**Résultat attendu** : Chambre créée avec ID (ex: 1)

#### Étape 2 : Créer un tenant
```bash
POST http://localhost:8083/api/tenants
Content-Type: application/json

{
  "contractId": 1,
  "userId": 1,
  "roomId": 1
}
```

**Vérifications** :
- ✅ Logs dans contrat-service : "🔗 [COMMUNICATION INTER-SERVICE] Vérification de l'utilisateur ID: 1"
- ✅ Logs dans contrat-service : "✅ [COMMUNICATION RÉUSSIE] Utilisateur trouvé"
- ✅ Logs dans contrat-service : "🔗 [COMMUNICATION INTER-SERVICE] Vérification de la chambre ID: 1"
- ✅ Logs dans contrat-service : "✅ [COMMUNICATION RÉUSSIE] Chambre trouvée - Disponible: true"
- ✅ Logs dans contrat-service : "🔄 [MISE À JOUR] Marquage de la chambre ID 1 comme non disponible"
- ✅ Tenant créé avec succès

#### Étape 3 : Vérifier que la chambre est maintenant non disponible
```bash
GET http://localhost:8082/api/rooms/1
```

**Résultat attendu** : `"isAvailable": false`

---

### Test 3 : Communication Kafka - Événements

#### Prérequis : Démarrer Kafka

**Option 1 : Avec Docker**
```bash
docker run -d --name kafka -p 9092:9092 apache/kafka:latest
```

**Option 2 : Installation locale**
```bash
# Télécharger Kafka depuis https://kafka.apache.org/downloads
# Démarrer Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Démarrer Kafka
bin/kafka-server-start.sh config/server.properties
```

#### Test : Créer un tenant et observer les événements Kafka

1. **Créer un tenant** (comme dans Test 2)

2. **Vérifier les logs** :

**Dans contrat-service** :
```
📤 [KAFKA] Événement publié: TENANT_CREATED pour tenant ID: 1 (roomId: 1)
```

**Dans annonce-service** :
```
📥 [KAFKA] Événement reçu: TENANT_CREATED pour contrat ID: 1
✅ [KAFKA] Chambre ID 1 marquée comme non disponible (tenant créé)
```

3. **Vérifier dans la base de données** :
```sql
SELECT * FROM rooms WHERE id = 1;
-- is_available devrait être false (0)
```

#### Test : Résilier un contrat et observer les événements

1. **Résilier un contrat** :
```bash
DELETE http://localhost:8083/api/contracts/1
```

2. **Vérifier les logs** :

**Dans contrat-service** :
```
📤 [KAFKA] Événement publié: CONTRACT_TERMINATED pour contrat ID: 1 (roomId: 1)
```

**Dans annonce-service** :
```
📥 [KAFKA] Événement reçu: CONTRACT_TERMINATED pour contrat ID: 1
✅ [KAFKA] Chambre ID 1 marquée comme disponible (contrat résilié)
```

3. **Vérifier dans la base de données** :
```sql
SELECT * FROM rooms WHERE id = 1;
-- is_available devrait être true (1)
```

---

### Test 4 : Scénario Complet

#### Scénario : Location complète d'une chambre

1. **Créer un utilisateur** (utilisateur-service)
2. **Créer une propriété** (annonce-service)
3. **Créer une chambre disponible** (annonce-service)
4. **Créer un contrat** (contrat-service) → Vérifie la propriété via OpenFeign
5. **Créer un tenant** (contrat-service) → 
   - Vérifie l'utilisateur via OpenFeign
   - Vérifie la chambre via OpenFeign
   - Marque la chambre comme non disponible via OpenFeign
   - Publie l'événement TENANT_CREATED via Kafka
6. **Vérifier** : La chambre est maintenant non disponible (via Kafka consumer)

---

## 🔍 Vérification des Communications

### Vérifier les Appels OpenFeign

**Dans les logs de contrat-service** :
```
🔗 [COMMUNICATION INTER-SERVICE] Vérification de la propriété ID: 1
   Service appelant: contrat-service
   Service appelé: annonce-service
   Endpoint: GET /api/properties/1
✅ [COMMUNICATION RÉUSSIE] Propriété trouvée: Appartement Test (ID: 1)
   Communication inter-service: contrat-service -> annonce-service
```

**Dans les logs de annonce-service** :
```
📥 [APPEL REÇU] GET /api/properties/1 - Peut être depuis un autre service via Feign
```

### Vérifier les Événements Kafka

**Option 1 : Via les logs**
- Chercher `📤 [KAFKA]` dans contrat-service
- Chercher `📥 [KAFKA]` dans annonce-service

**Option 2 : Via Kafka Console Consumer**
```bash
# Consulter les messages du topic
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic contract-events \
  --from-beginning
```

**Option 3 : Via la base de données**
```sql
-- Vérifier la disponibilité des chambres
SELECT id, name, is_available FROM rooms;

-- Vérifier les contrats
SELECT id, property_id, status FROM contracts;

-- Vérifier les tenants
SELECT id, contract_id, user_id, room_id FROM tenants;
```

---

## 🐛 Dépannage

### Problème : OpenFeign ne trouve pas le service

**Symptôme** : `Service 'annonce-service' not found`

**Solutions** :
1. Vérifier que Eureka Server est démarré
2. Vérifier que annonce-service est enregistré dans Eureka
3. Vérifier le nom du service dans `@FeignClient(name = "annonce-service")`
4. Vérifier les logs Eureka : `http://localhost:8761`

### Problème : Kafka ne publie pas les événements

**Symptôme** : Pas de logs `📤 [KAFKA]`

**Solutions** :
1. Vérifier que Kafka est démarré : `telnet localhost 9092`
2. Vérifier la configuration dans `KafkaProducerConfig`
3. Vérifier les logs d'erreur Kafka

### Problème : Kafka ne consomme pas les événements

**Symptôme** : Pas de logs `📥 [KAFKA]`

**Solutions** :
1. Vérifier que le topic existe : `kafka-topics.sh --list --bootstrap-server localhost:9092`
2. Vérifier la configuration dans `KafkaConsumerConfig`
3. Vérifier le group-id : doit être unique par service
4. Vérifier les logs d'erreur Kafka

### Problème : La chambre n'est pas marquée comme non disponible

**Vérifications** :
1. Vérifier que l'événement Kafka est publié (logs)
2. Vérifier que l'événement Kafka est consommé (logs)
3. Vérifier que `roomId` n'est pas null dans l'événement
4. Vérifier que la chambre existe dans la base

---

## 📊 Résumé des Communications

| Source | Cible | Type | Technologie | Statut | Topic/Endpoint |
|--------|-------|------|-------------|--------|----------------|
| Contrat | Utilisateur | Synchrone | OpenFeign | ✅ | GET /api/users/{id} |
| Annonce | Utilisateur | Synchrone | OpenFeign | ✅ | GET /api/users/{id} |
| Contrat | Annonce | Synchrone | OpenFeign | ✅ | GET /api/properties/{id} |
| Contrat | Annonce | Synchrone | OpenFeign | ✅ | GET /api/rooms/{id} |
| Contrat | Annonce | Synchrone | OpenFeign | ✅ | PUT /api/rooms/{id}/availability |
| Contrat | Annonce | Asynchrone | Kafka | ✅ | contract-events |

---

## 🚀 Prochaines Étapes Recommandées

1. **Circuit Breaker** : Ajouter Resilience4j pour gérer les pannes
2. **Retry Logic** : Configurer les retries pour OpenFeign
3. **Monitoring** : Ajouter Micrometer pour surveiller les appels
4. **Plus de Topics Kafka** :
   - `user-events` : Pour synchroniser les utilisateurs
   - `property-events` : Pour notifier les changements de propriétés
   - `ad-events` : Pour synchroniser les annonces

---

**Toutes les communications sont maintenant implémentées et testables ! 🎉**

