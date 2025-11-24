# 🧪 Guide de Test - Communication Inter-Services

## 🎯 Objectif

Tester que les services communiquent correctement entre eux via Feign Client.

---

## ✅ Prérequis

1. **Tous les services doivent être démarrés :**
   - Eureka Server (8761)
   - Gateway Service (8080)
   - Utilisateur Service (8084)
   - Contrat Service (8083)
   - Annonce Service (8082)

2. **Vérifier Eureka :** http://localhost:8761
   - Tous les services doivent être "UP"

3. **Ouvrir Postman**

---

## 📋 Scénarios de Test

### Scénario 1 : Créer un Tenant (Communication Contrat → Utilisateur)

#### Étape 1 : Vérifier qu'un utilisateur existe
```
GET http://localhost:8080/api/users
```
**Résultat :** Liste des utilisateurs (notez un ID, ex: `id: 1`)

#### Étape 2 : Créer un contrat
```
POST http://localhost:8080/api/contracts
Content-Type: application/json

{
  "propertyId": 1,
  "startDate": "2025-12-01",
  "endDate": "2026-12-01",
  "status": "ACTIVE"
}
```
**Résultat :** Contrat créé (notez l'ID, ex: `id: 1`)

#### Étape 3 : Créer un tenant (COMMUNICATION INTER-SERVICE)
```
POST http://localhost:8080/api/tenants
Content-Type: application/json

{
  "contractId": 1,
  "userId": 1,
  "roomId": 1
}
```

**✅ Résultat attendu :**
- **Status :** 201 Created
- **Body :** Tenant créé avec les données
- **Logs Contrat Service :**
  ```
  Vérification de l'existence de l'utilisateur ID: 1 via communication inter-service
  Utilisateur trouvé: John Doe (ID: 1)
  Communication inter-service réussie: contrat-service -> utilisateur-service
  ```

#### Étape 4 : Tester avec utilisateur inexistant
```
POST http://localhost:8080/api/tenants
Content-Type: application/json

{
  "contractId": 1,
  "userId": 999,  ← N'existe pas
  "roomId": 1
}
```

**❌ Résultat attendu :**
- **Status :** 500 Internal Server Error
- **Message :** "Utilisateur avec ID 999 n'existe pas"
- **Logs :** "Utilisateur ID 999 non trouvé dans utilisateur-service"

---

### Scénario 2 : Créer une Annonce (Communication Annonce → Utilisateur)

#### Étape 1 : Créer une propriété
```
POST http://localhost:8080/api/properties
Content-Type: application/json

{
  "title": "Appartement Test",
  "address": "123 Rue Test",
  "city": "Casablanca",
  "description": "Test de communication inter-service",
  "ownerId": 1
}
```

#### Étape 2 : Créer une annonce (COMMUNICATION INTER-SERVICE)
```
POST http://localhost:8080/api/ads
Content-Type: application/json

{
  "propertyId": 1,
  "title": "Annonce Test Communication",
  "description": "Test de la communication inter-service",
  "ownerId": 1,  ← Vérification via Feign
  "status": "PUBLISHED"
}
```

**✅ Résultat attendu :**
- **Status :** 201 Created
- **Logs Annonce Service :**
  ```
  Vérification du propriétaire ID: 1 via communication inter-service
  Propriétaire trouvé: John Doe (ID: 1)
  Communication inter-service réussie: annonce-service -> utilisateur-service
  ```

---

## 🔍 Observer la Communication

### 1. Dans les Logs

**Ouvrez les terminaux des services et cherchez :**

**Contrat Service :**
```
[INFO] Vérification de l'existence de l'utilisateur ID: 1 via communication inter-service
[INFO] Utilisateur trouvé: John Doe (ID: 1)
[INFO] Communication inter-service réussie: contrat-service -> utilisateur-service
```

**Annonce Service :**
```
[INFO] Vérification du propriétaire ID: 1 via communication inter-service
[INFO] Propriétaire trouvé: John Doe (ID: 1)
[INFO] Communication inter-service réussie: annonce-service -> utilisateur-service
```

**Utilisateur Service :**
```
[INFO] GET /api/users/1  ← Appel reçu depuis un autre service
```

### 2. Dans Eureka Dashboard

1. Ouvrez http://localhost:8761
2. Vous verrez tous les services connectés
3. Quand un service appelle un autre, Eureka résout automatiquement le nom

### 3. Activer les Logs Feign (Optionnel)

Pour voir les détails des appels HTTP, ajoutez dans `application.properties` :

**contrat-service/src/main/resources/application.properties :**
```properties
logging.level.emsi.ma.contratservice.client=DEBUG
```

**annonce-service/src/main/resources/application.properties :**
```properties
logging.level.emsi.ma.annonceservice.client=DEBUG
```

Vous verrez alors dans les logs :
```
[DEBUG] ---> GET http://utilisateur-service/api/users/1 HTTP/1.1
[DEBUG] <--- HTTP/1.1 200 OK (123ms)
```

---

## 📊 Flux de Communication

### Exemple : Créer un Tenant

```
1. Client (Postman)
   ↓
   POST http://localhost:8080/api/tenants
   ↓
2. Gateway Service (port 8080)
   ↓ Route vers
   ↓
3. Contrat Service (port 8083)
   ↓ TenantServiceImpl.create()
   ↓
4. Feign Client appelle
   ↓
5. Utilisateur Service (port 8084)
   ↓ GET /api/users/1
   ↓
6. Réponse : UserResponseDto
   ↓
7. Contrat Service continue
   ↓ Crée le tenant
   ↓
8. Réponse au client
```

---

## 🎯 Checklist de Test

- [ ] ✅ Créer un tenant avec utilisateur existant → Succès
- [ ] ❌ Créer un tenant avec utilisateur inexistant → Erreur
- [ ] ✅ Créer une annonce avec propriétaire existant → Succès
- [ ] ❌ Créer une annonce avec propriétaire inexistant → Erreur
- [ ] ✅ Vérifier les logs montrent la communication
- [ ] ✅ Vérifier Eureka montre tous les services

---

## 🐛 Dépannage

### Erreur : "Connection refused"

**Cause :** Le service appelé n'est pas démarré

**Solution :**
1. Vérifiez que tous les services sont démarrés
2. Vérifiez Eureka : http://localhost:8761

### Erreur : "Service not found"

**Cause :** Le service n'est pas enregistré dans Eureka

**Solution :**
1. Attendez 30-60 secondes après le démarrage
2. Vérifiez que `@EnableDiscoveryClient` est présent
3. Vérifiez la configuration Eureka dans `application.properties`

### Erreur : "Read timeout"

**Cause :** Le service appelé met trop de temps à répondre

**Solution :**
Ajoutez dans `application.properties` :
```properties
feign.client.config.default.readTimeout=10000
```

---

## 📝 Résumé

La communication inter-services se fait via **Feign Client** :

1. **Déclaration :** Interface avec `@FeignClient`
2. **Découverte :** Eureka résout automatiquement le nom du service
3. **Appel :** Injection du client et appel comme une méthode Java normale
4. **Observation :** Logs dans les services appelant et appelé

**Testez maintenant avec les scénarios ci-dessus ! 🚀**

