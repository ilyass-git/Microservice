# 🔗 Communication Inter-Services

## 📖 Vue d'Ensemble

Dans une architecture de microservices, les services doivent communiquer entre eux. Dans ce projet, nous utilisons **Spring Cloud OpenFeign** pour la communication inter-services.

---

## 🎯 Comment ça fonctionne ?

### 1. Service Discovery (Eureka)

Tous les services s'enregistrent auprès d'**Eureka Server**. Quand un service veut appeler un autre service :

1. Il demande à Eureka : "Où se trouve `utilisateur-service` ?"
2. Eureka répond avec l'URL (ex: `http://localhost:8084`)
3. Le service fait l'appel HTTP

### 2. Feign Client

**Feign** est une bibliothèque qui simplifie les appels HTTP entre services :

- ✅ **Découverte automatique** via Eureka
- ✅ **Code déclaratif** (interfaces Java)
- ✅ **Gestion automatique** des erreurs et timeouts
- ✅ **Load balancing** automatique

---

## 📍 Où voir la Communication ?

### 1. Dans le Code

#### Exemple : Contrat Service → Utilisateur Service

**Fichier :** `contrat-service/src/main/java/emsi/ma/contratservice/client/UserServiceClient.java`

```java
@FeignClient(name = "utilisateur-service", path = "/api/users")
public interface UserServiceClient {
    @GetMapping("/{id}")
    UserResponseDto getUserById(@PathVariable Long id);
}
```

**Utilisation dans le service :**

```java
@Service
public class TenantServiceImpl {
    private final UserServiceClient userServiceClient;
    
    public Tenant create(Tenant tenant) {
        // Appel inter-service
        var user = userServiceClient.getUserById(tenant.getUserId());
        if (user == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        return tenantRepository.save(tenant);
    }
}
```

### 2. Dans les Logs

Quand un service appelle un autre service, vous verrez dans les logs :

```
[contrat-service] Vérification de l'existence de l'utilisateur ID: 1 via communication inter-service
[contrat-service] Utilisateur trouvé: John Doe (ID: 1)
[contrat-service] Communication inter-service réussie: contrat-service -> utilisateur-service
```

### 3. Dans Eureka Dashboard

**URL :** http://localhost:8761

Vous verrez tous les services enregistrés. Quand un service appelle un autre via Feign, Eureka résout automatiquement le nom du service.

---

## 🔍 Exemples Concrets dans le Projet

### Exemple 1 : Contrat Service appelle Utilisateur Service

**Scénario :** Avant de créer un `Tenant`, on vérifie que l'utilisateur existe.

**Fichiers impliqués :**
- `contrat-service/.../client/UserServiceClient.java` - Interface Feign
- `contrat-service/.../service/impl/TenantServiceImpl.java` - Utilisation du client

**Flux :**
```
1. POST /api/tenants (via Gateway ou directement)
   ↓
2. TenantServiceImpl.create()
   ↓
3. userServiceClient.getUserById(userId)  ← APPEL INTER-SERVICE
   ↓
4. Utilisateur Service répond avec UserResponseDto
   ↓
5. Si utilisateur existe → Créer le tenant
   Si utilisateur n'existe pas → Erreur
```

### Exemple 2 : Annonce Service appelle Utilisateur Service

**Scénario :** Avant de créer une `Ad`, on vérifie que le propriétaire existe.

**Fichiers impliqués :**
- `annonce-service/.../client/UserServiceClient.java` - Interface Feign
- `annonce-service/.../service/impl/AdServiceImpl.java` - Utilisation du client

**Flux :**
```
1. POST /api/ads
   ↓
2. AdServiceImpl.create()
   ↓
3. userServiceClient.getUserById(ownerId)  ← APPEL INTER-SERVICE
   ↓
4. Utilisateur Service répond
   ↓
5. Si propriétaire existe → Créer l'annonce
```

---

## 🧪 Comment Tester la Communication Inter-Service

### Test 1 : Créer un Tenant avec Utilisateur Existant

**Étape 1 :** Vérifier qu'un utilisateur existe
```
GET http://localhost:8080/api/users
```
Notez l'ID d'un utilisateur (ex: `id: 1`)

**Étape 2 :** Créer un contrat
```
POST http://localhost:8080/api/contracts
Body:
{
  "propertyId": 1,
  "startDate": "2025-12-01",
  "endDate": "2026-12-01",
  "status": "ACTIVE"
}
```
Notez l'ID du contrat créé (ex: `id: 1`)

**Étape 3 :** Créer un tenant (COMMUNICATION INTER-SERVICE)
```
POST http://localhost:8080/api/tenants
Body:
{
  "contractId": 1,
  "userId": 1,  ← Cet utilisateur doit exister
  "roomId": 1
}
```

**Résultat attendu :**
- ✅ **200 OK** si l'utilisateur existe
- ✅ Dans les logs de `contrat-service`, vous verrez :
  ```
  Vérification de l'existence de l'utilisateur ID: 1 via communication inter-service
  Utilisateur trouvé: John Doe (ID: 1)
  Communication inter-service réussie: contrat-service -> utilisateur-service
  ```

### Test 2 : Créer un Tenant avec Utilisateur Inexistant

```
POST http://localhost:8080/api/tenants
Body:
{
  "contractId": 1,
  "userId": 999,  ← Cet utilisateur n'existe pas
  "roomId": 1
}
```

**Résultat attendu :**
- ❌ **500 Internal Server Error**
- ❌ Message : "Utilisateur avec ID 999 n'existe pas"
- ✅ Dans les logs : "Utilisateur ID 999 non trouvé dans utilisateur-service"

### Test 3 : Créer une Annonce avec Propriétaire

**Étape 1 :** Créer une propriété
```
POST http://localhost:8080/api/properties
Body:
{
  "title": "Appartement test",
  "address": "123 Rue Test",
  "city": "Casablanca",
  "description": "Test",
  "ownerId": 1  ← Utilisateur existant
}
```

**Étape 2 :** Créer une annonce (COMMUNICATION INTER-SERVICE)
```
POST http://localhost:8080/api/ads
Body:
{
  "propertyId": 1,
  "title": "Annonce test",
  "description": "Test",
  "ownerId": 1,  ← Vérification inter-service
  "status": "PUBLISHED"
}
```

**Résultat attendu :**
- ✅ **201 Created** si le propriétaire existe
- ✅ Logs montrant la communication inter-service

---

## 📊 Observer la Communication

### 1. Logs des Services

**Contrat Service :**
```bash
# Cherchez ces logs :
"Vérification de l'existence de l'utilisateur"
"Communication inter-service réussie"
"Erreur lors de la communication avec utilisateur-service"
```

**Annonce Service :**
```bash
# Cherchez ces logs :
"Vérification du propriétaire"
"Propriétaire trouvé"
"Communication inter-service réussie"
```

**Utilisateur Service :**
```bash
# Quand il reçoit un appel :
"GET /api/users/{id}"  ← Appel reçu depuis un autre service
```

### 2. Eureka Dashboard

1. Ouvrez http://localhost:8761
2. Cliquez sur un service (ex: `CONTRAT-SERVICE`)
3. Vous verrez les métadonnées et les instances

### 3. Network Tab (Postman/DevTools)

Dans Postman, activez le **Console** pour voir les requêtes HTTP. Vous verrez :
- La requête vers le Gateway
- Le Gateway qui route vers le service
- Le service qui appelle un autre service (via Feign)

---

## 🔧 Configuration Feign

### Timeout et Retry

Par défaut, Feign a un timeout. Pour le configurer, ajoutez dans `application.properties` :

```properties
# Timeout pour les appels Feign (en millisecondes)
feign.client.config.default.connectTimeout=5000
feign.client.config.default.readTimeout=10000

# Activer les logs Feign (pour debug)
logging.level.emsi.ma.contratservice.client=DEBUG
```

### Gestion des Erreurs

Feign peut gérer automatiquement les erreurs. Exemple avec un fallback :

```java
@FeignClient(name = "utilisateur-service", 
             path = "/api/users",
             fallback = UserServiceClientFallback.class)
public interface UserServiceClient {
    // ...
}
```

---

## 📝 Structure des Clients Feign

```
contrat-service/
└── src/main/java/emsi/ma/contratservice/
    └── client/
        ├── UserServiceClient.java          ← Interface Feign
        └── dto/
            └── UserResponseDto.java        ← DTO partagé

annonce-service/
└── src/main/java/emsi/ma/annonceservice/
    └── client/
        ├── UserServiceClient.java          ← Interface Feign
        └── dto/
            └── UserResponseDto.java        ← DTO partagé
```

---

## 🎯 Avantages de Feign

1. **Découverte automatique** : Pas besoin de connaître l'URL exacte
2. **Load balancing** : Si plusieurs instances, Feign répartit la charge
3. **Code simple** : Juste une interface Java
4. **Type-safe** : Compilation vérifie les types
5. **Intégration Spring** : Injection de dépendances automatique

---

## ⚠️ Bonnes Pratiques

1. **Toujours vérifier les réponses** : Un service peut être down
2. **Gérer les timeouts** : Configurer des timeouts appropriés
3. **Logging** : Logger les appels inter-services pour le debug
4. **DTOs séparés** : Créer des DTOs dans le package `client/dto`
5. **Gestion d'erreurs** : Implémenter des fallbacks si nécessaire

---

## 🚀 Prochaines Étapes

- [ ] Ajouter Circuit Breaker (Resilience4j) pour gérer les pannes
- [ ] Implémenter des fallbacks pour les appels Feign
- [ ] Ajouter du tracing distribué (Sleuth/Zipkin)
- [ ] Configurer des timeouts personnalisés
- [ ] Ajouter de la retry logic

---

**La communication inter-services est maintenant active ! Testez-la avec les exemples ci-dessus. 🎉**

