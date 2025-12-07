# 🧪 Plan de Test des Communications Inter-Services

## 📋 Vue d'Ensemble

Ce document explique comment je vais tester toutes les communications REST (OpenFeign) et Kafka entre les microservices.

## 🎯 Objectif

Vérifier que toutes les communications inter-services fonctionnent correctement :
- **Communications REST (OpenFeign)** : Appels synchrones entre services
- **Communications Kafka** : Événements asynchrones entre services

---

## 🔍 Ce que je vais tester

### 1. Communications REST (OpenFeign)

#### 1.1. Annonce Service → Utilisateur Service
- **Client Feign** : `UserServiceClient`
- **Endpoint** : `GET /api/users/{id}`
- **Test** : Créer une propriété avec un `ownerId` → Vérifie que l'utilisateur existe
- **Vérification** : 
  - ✅ La propriété est créée si l'utilisateur existe
  - ✅ Erreur si l'utilisateur n'existe pas
  - ✅ Logs montrent la communication inter-service

#### 1.2. Contrat Service → Utilisateur Service
- **Client Feign** : `UserServiceClient`
- **Endpoint** : `GET /api/users/{id}`
- **Test** : Créer un tenant avec un `userId` → Vérifie que l'utilisateur existe
- **Vérification** :
  - ✅ Le tenant est créé si l'utilisateur existe
  - ✅ Erreur si l'utilisateur n'existe pas
  - ✅ Logs montrent la communication inter-service

#### 1.3. Contrat Service → Annonce Service (Property)
- **Client Feign** : `PropertyServiceClient`
- **Endpoint** : `GET /api/properties/{id}`
- **Test** : Créer un contrat avec un `propertyId` → Vérifie que la propriété existe
- **Vérification** :
  - ✅ Le contrat est créé si la propriété existe
  - ✅ Erreur si la propriété n'existe pas
  - ✅ Logs montrent la communication inter-service

#### 1.4. Contrat Service → Annonce Service (Room - GET)
- **Client Feign** : `RoomServiceClient`
- **Endpoint** : `GET /api/rooms/{id}`
- **Test** : Créer un tenant avec un `roomId` → Vérifie que la chambre existe et est disponible
- **Vérification** :
  - ✅ Le tenant est créé si la chambre existe et est disponible
  - ✅ Erreur si la chambre n'existe pas ou n'est pas disponible
  - ✅ Logs montrent la communication inter-service

#### 1.5. Contrat Service → Annonce Service (Room - PUT)
- **Client Feign** : `RoomServiceClient`
- **Endpoint** : `PUT /api/rooms/{id}/availability`
- **Test** : Créer un tenant → Marque la chambre comme non disponible
- **Vérification** :
  - ✅ La chambre est marquée comme non disponible (`isAvailable: false`)
  - ✅ Logs montrent la mise à jour

---

### 2. Communications Kafka (Asynchrones)

#### 2.1. Événement CONTRACT_CREATED
- **Producteur** : `ContractServiceImpl.create()` (contrat-service)
- **Topic** : `contract-events`
- **Test** : Créer un contrat
- **Vérification** :
  - ✅ Logs montrent `📤 [KAFKA] Événement publié: CONTRACT_CREATED`
  - ✅ L'événement est publié dans le topic Kafka

#### 2.2. Événement TENANT_CREATED
- **Producteur** : `TenantServiceImpl.create()` (contrat-service)
- **Topic** : `contract-events`
- **Consommateur** : `ContractEventConsumer.handleTenantCreated()` (annonce-service)
- **Test** : Créer un tenant avec un `roomId`
- **Vérification** :
  - ✅ Logs montrent `📤 [KAFKA] Événement publié: TENANT_CREATED`
  - ✅ Logs montrent `📥 [KAFKA] Événement reçu: TENANT_CREATED`
  - ✅ La chambre est marquée comme non disponible via Kafka Consumer
  - ✅ La chambre a `isAvailable: false` dans la base de données

#### 2.3. Événement CONTRACT_ACTIVATED
- **Producteur** : `ContractServiceImpl.update()` (contrat-service)
- **Topic** : `contract-events`
- **Test** : Activer un contrat (changer le statut à `ACTIVE`)
- **Vérification** :
  - ✅ Logs montrent `📤 [KAFKA] Événement publié: CONTRACT_ACTIVATED`
  - ✅ L'événement est publié dans le topic Kafka

#### 2.4. Événement CONTRACT_TERMINATED
- **Producteur** : `ContractServiceImpl.delete()` (contrat-service)
- **Topic** : `contract-events`
- **Consommateur** : `ContractEventConsumer.handleContractTerminated()` (annonce-service)
- **Test** : Résilier un contrat (supprimer un contrat avec des tenants)
- **Vérification** :
  - ✅ Logs montrent `📤 [KAFKA] Événement publié: CONTRACT_TERMINATED`
  - ✅ Logs montrent `📥 [KAFKA] Événement reçu: CONTRACT_TERMINATED`
  - ✅ La chambre est marquée comme disponible via Kafka Consumer
  - ✅ La chambre a `isAvailable: true` dans la base de données

---

## 🚀 Comment je vais tester

### Étape 1 : Vérification des Services
1. Vérifier que tous les services sont démarrés :
   - ✅ Eureka Server (8761)
   - ✅ Config Server (8888)
   - ✅ Utilisateur Service (8084)
   - ✅ Annonce Service (8082)
   - ✅ Contrat Service (8083)
   - ✅ Gateway Service (8080)
   - ✅ Kafka (9092)
   - ✅ Zookeeper (2181)

### Étape 2 : Test des Communications REST
1. **Créer un utilisateur** (utilisateur-service)
   - POST `/api/users`
   - Stocker l'ID pour les tests suivants

2. **Créer une propriété** (annonce-service)
   - POST `/api/properties` avec `ownerId`
   - ✅ Vérifie que la communication OpenFeign Annonce → Utilisateur fonctionne
   - Stocker l'ID de la propriété

3. **Créer une chambre disponible** (annonce-service)
   - POST `/api/rooms` avec `propertyId` et `isAvailable: true`
   - Vérifier que la chambre est disponible
   - Stocker l'ID de la chambre

4. **Créer un contrat** (contrat-service)
   - POST `/api/contracts` avec `propertyId`
   - ✅ Vérifie que la communication OpenFeign Contrat → Annonce (Property) fonctionne
   - ✅ Vérifie que l'événement Kafka CONTRACT_CREATED est publié
   - Stocker l'ID du contrat

5. **Créer un tenant** (contrat-service)
   - POST `/api/tenants` avec `contractId`, `userId`, `roomId`
   - ✅ Vérifie que la communication OpenFeign Contrat → Utilisateur fonctionne
   - ✅ Vérifie que la communication OpenFeign Contrat → Annonce (Room GET) fonctionne
   - ✅ Vérifie que la communication OpenFeign Contrat → Annonce (Room PUT) fonctionne
   - ✅ Vérifie que la chambre est marquée comme non disponible
   - ✅ Vérifie que l'événement Kafka TENANT_CREATED est publié
   - ✅ Vérifie que le consommateur Kafka met à jour la chambre
   - Stocker l'ID du tenant

### Étape 3 : Test des Communications Kafka
1. **Vérifier la synchronisation via Kafka**
   - Attendre quelques secondes
   - GET `/api/rooms/{id}` pour vérifier que `isAvailable: false`
   - ✅ Vérifie que Kafka a bien synchronisé les données

2. **Activer le contrat**
   - PUT `/api/contracts/{id}` avec `status: ACTIVE`
   - ✅ Vérifie que l'événement Kafka CONTRACT_ACTIVATED est publié

3. **Résilier le contrat**
   - DELETE `/api/contracts/{id}`
   - ✅ Vérifie que l'événement Kafka CONTRACT_TERMINATED est publié
   - ✅ Vérifie que le consommateur Kafka libère la chambre
   - GET `/api/rooms/{id}` pour vérifier que `isAvailable: true`

### Étape 4 : Vérification des Logs
1. **Logs OpenFeign** :
   - Chercher `🔗 [COMMUNICATION INTER-SERVICE]` dans les logs
   - Chercher `✅ [COMMUNICATION RÉUSSIE]` dans les logs
   - Chercher `❌ [COMMUNICATION ÉCHOUÉE]` pour les erreurs

2. **Logs Kafka Producteur** :
   - Chercher `📤 [KAFKA] Événement publié` dans les logs de contrat-service

3. **Logs Kafka Consommateur** :
   - Chercher `📥 [KAFKA] Événement reçu` dans les logs de annonce-service
   - Chercher `✅ [KAFKA] Chambre ID X marquée comme...` dans les logs

---

## 📊 Résultats Attendus

### Communications REST (OpenFeign)
| Communication | Statut Attendu | Vérification |
|--------------|----------------|---------------|
| Annonce → Utilisateur | ✅ | Propriété créée si utilisateur existe |
| Contrat → Utilisateur | ✅ | Tenant créé si utilisateur existe |
| Contrat → Annonce (Property) | ✅ | Contrat créé si propriété existe |
| Contrat → Annonce (Room GET) | ✅ | Tenant créé si chambre existe et disponible |
| Contrat → Annonce (Room PUT) | ✅ | Chambre marquée comme non disponible |

### Communications Kafka
| Événement | Producteur | Consommateur | Action |
|-----------|------------|--------------|--------|
| CONTRACT_CREATED | ✅ | - | Publié lors de la création |
| TENANT_CREATED | ✅ | ✅ | Chambre marquée non disponible |
| CONTRACT_ACTIVATED | ✅ | - | Publié lors de l'activation |
| CONTRACT_TERMINATED | ✅ | ✅ | Chambre marquée disponible |

---

## 🛠️ Outils de Test

### Script PowerShell
J'ai créé un script `test-communications.ps1` qui :
- ✅ Vérifie que tous les services sont accessibles
- ✅ Teste toutes les communications REST
- ✅ Teste toutes les communications Kafka
- ✅ Vérifie la synchronisation des données
- ✅ Affiche un résumé des tests

### Commandes Manuelles
Si vous préférez tester manuellement :

```powershell
# 1. Créer un utilisateur
Invoke-RestMethod -Method POST -Uri "http://localhost:8084/api/users" `
  -ContentType "application/json" `
  -Body '{"prenom":"Test","nom":"User","email":"test@example.com","telephone":"0612345678","dateNaissance":"1990-01-01"}'

# 2. Créer une propriété
Invoke-RestMethod -Method POST -Uri "http://localhost:8082/api/properties" `
  -ContentType "application/json" `
  -Body '{"title":"Appartement Test","address":"123 Rue Test","city":"Casablanca","description":"Test","ownerId":1}'

# 3. Créer une chambre
Invoke-RestMethod -Method POST -Uri "http://localhost:8082/api/rooms" `
  -ContentType "application/json" `
  -Body '{"propertyId":1,"name":"Chambre 1","price":2000,"isAvailable":true}'

# 4. Créer un contrat
Invoke-RestMethod -Method POST -Uri "http://localhost:8083/api/contracts" `
  -ContentType "application/json" `
  -Body '{"propertyId":1,"startDate":"2025-12-01","endDate":"2026-12-01","status":"DRAFT"}'

# 5. Créer un tenant
Invoke-RestMethod -Method POST -Uri "http://localhost:8083/api/tenants" `
  -ContentType "application/json" `
  -Body '{"contractId":1,"userId":1,"roomId":1}'

# 6. Vérifier la chambre (devrait être non disponible)
Invoke-RestMethod -Method GET -Uri "http://localhost:8082/api/rooms/1"

# 7. Résilier le contrat
Invoke-RestMethod -Method DELETE -Uri "http://localhost:8083/api/contracts/1"

# 8. Vérifier la chambre (devrait être disponible)
Invoke-RestMethod -Method GET -Uri "http://localhost:8082/api/rooms/1"
```

---

## ✅ Checklist de Vérification

### Avant de commencer
- [ ] Tous les services sont démarrés
- [ ] Kafka et Zookeeper sont démarrés
- [ ] Eureka montre tous les services enregistrés
- [ ] Les bases de données sont accessibles

### Après les tests
- [ ] Toutes les communications REST fonctionnent
- [ ] Tous les événements Kafka sont publiés
- [ ] Tous les événements Kafka sont consommés
- [ ] Les données sont synchronisées entre services
- [ ] Les logs montrent les communications réussies

---

## 🐛 Dépannage

### Problème : Service non accessible
- Vérifier que le service est démarré
- Vérifier le port dans `application.properties`
- Vérifier les logs du service

### Problème : Communication OpenFeign échoue
- Vérifier que Eureka est démarré
- Vérifier que le service cible est enregistré dans Eureka
- Vérifier le nom du service dans `@FeignClient(name = "...")`
- Vérifier les logs pour les erreurs de connexion

### Problème : Événements Kafka non publiés
- Vérifier que Kafka est démarré (port 9092)
- Vérifier la configuration dans `KafkaProducerConfig`
- Vérifier les logs pour les erreurs Kafka

### Problème : Événements Kafka non consommés
- Vérifier que le topic existe
- Vérifier la configuration dans `KafkaConsumerConfig`
- Vérifier le `groupId` (doit être unique)
- Vérifier les logs pour les erreurs de consommation

---

## 📝 Conclusion

Ce plan de test couvre :
- ✅ **5 communications REST** (OpenFeign)
- ✅ **4 événements Kafka** (avec consommation)
- ✅ **Synchronisation des données** entre services
- ✅ **Vérification des logs** pour chaque communication

Tous les tests sont automatisés dans le script `test-communications.ps1` pour faciliter l'exécution et la répétition des tests.

