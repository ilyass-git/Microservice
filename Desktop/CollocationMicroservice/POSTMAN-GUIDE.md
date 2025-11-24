# 🚀 Guide Postman - Test des APIs

## 📋 Prérequis

1. **Lancer tous les services** dans l'ordre :
   - Eureka Server (port 8761)
   - Gateway Service (port 8080) - **Point d'entrée principal**
   - Annonce Service (port 8082)
   - Contrat Service (port 8083)
   - Utilisateur Service (port 8084)

2. **Vérifier que les services sont enregistrés** sur http://localhost:8761

3. **Ouvrir Postman**

---

## 🌐 URLs de Base

### Via Gateway (Recommandé)
```
http://localhost:8080/api/{endpoint}
```

### Accès Direct aux Services
```
http://localhost:8082/api/{endpoint}  (Annonce Service)
http://localhost:8083/api/{endpoint}  (Contrat Service)
http://localhost:8084/api/{endpoint}  (Utilisateur Service)
```

---

## 👥 Utilisateur Service

### 1. Créer un Utilisateur
**POST** `http://localhost:8080/api/users`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "email": "test@example.com",
  "password": "password123",
  "nom": "Test",
  "prenom": "User",
  "telephone": "0612345678"
}
```

**Réponse attendue:** 201 Created avec les données de l'utilisateur (sans password)

---

### 2. Récupérer tous les Utilisateurs
**GET** `http://localhost:8080/api/users`

**Réponse attendue:** 200 OK avec liste des utilisateurs

---

### 3. Récupérer un Utilisateur par ID
**GET** `http://localhost:8080/api/users/1`

**Réponse attendue:** 200 OK avec les données de l'utilisateur

---

### 4. Récupérer un Utilisateur par Email
**GET** `http://localhost:8080/api/users/email/john.doe@example.com`

**Réponse attendue:** 200 OK avec les données de l'utilisateur

---

### 5. Mettre à jour un Utilisateur
**PUT** `http://localhost:8080/api/users/1`

**Body (raw JSON):**
```json
{
  "id": 1,
  "email": "updated@example.com",
  "password": "newpassword",
  "nom": "Updated",
  "prenom": "Name",
  "telephone": "0698765432"
}
```

---

### 6. Supprimer un Utilisateur
**DELETE** `http://localhost:8080/api/users/1`

**Réponse attendue:** 204 No Content

---

### 7. Créer un Profil
**POST** `http://localhost:8080/api/profiles`

**Body (raw JSON):**
```json
{
  "userId": 1,
  "bio": "Étudiant en informatique",
  "age": 22,
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

---

### 8. Récupérer un Profil par User ID
**GET** `http://localhost:8080/api/profiles/user/1`

---

### 9. Créer une Préférence
**POST** `http://localhost:8080/api/preferences`

**Body (raw JSON):**
```json
{
  "userId": 1,
  "budget": 2500.00,
  "city": "Casablanca",
  "smokingAllowed": false
}
```

---

## 🏠 Annonce Service

### 1. Créer une Propriété
**POST** `http://localhost:8080/api/properties`

**Body (raw JSON):**
```json
{
  "title": "Appartement 3 pièces",
  "address": "123 Rue Example",
  "city": "Casablanca",
  "description": "Bel appartement au centre-ville",
  "ownerId": 1
}
```

---

### 2. Récupérer toutes les Propriétés
**GET** `http://localhost:8080/api/properties`

---

### 3. Récupérer les Propriétés par Ville
**GET** `http://localhost:8080/api/properties/city/Casablanca`

---

### 4. Créer une Chambre
**POST** `http://localhost:8080/api/rooms`

**Body (raw JSON):**
```json
{
  "propertyId": 1,
  "name": "Chambre 1",
  "price": 2000.00,
  "isAvailable": true
}
```

---

### 5. Récupérer les Chambres Disponibles
**GET** `http://localhost:8080/api/rooms/available`

---

### 6. Récupérer les Chambres par Prix Maximum
**GET** `http://localhost:8080/api/rooms/price?maxPrice=2500`

---

### 7. Créer une Annonce
**POST** `http://localhost:8080/api/ads`

**Body (raw JSON):**
```json
{
  "propertyId": 1,
  "roomId": 1,
  "title": "Chambre disponible - Centre Casablanca",
  "description": "Chambre spacieuse dans appartement partagé",
  "photoUrls": [
    "https://example.com/photo1.jpg",
    "https://example.com/photo2.jpg"
  ],
  "ownerId": 1,
  "status": "PUBLISHED"
}
```

**Note:** Les valeurs possibles pour `status` sont : `DRAFT`, `PUBLISHED`, `CLOSED`

---

### 8. Récupérer toutes les Annonces Publiées
**GET** `http://localhost:8080/api/ads/published`

---

### 9. Rechercher des Annonces par Titre
**GET** `http://localhost:8080/api/ads/search?keyword=chambre`

---

### 10. Mettre à jour le Statut d'une Annonce
**PUT** `http://localhost:8080/api/ads/1/status?status=CLOSED`

---

## 📄 Contrat Service

### 1. Créer un Contrat
**POST** `http://localhost:8080/api/contracts`

**Body (raw JSON):**
```json
{
  "propertyId": 1,
  "startDate": "2025-12-01",
  "endDate": "2026-12-01",
  "status": "ACTIVE"
}
```

**Note:** Format de date : `YYYY-MM-DD`  
**Status possibles :** `DRAFT`, `ACTIVE`, `TERMINATED`

---

### 2. Récupérer tous les Contrats
**GET** `http://localhost:8080/api/contracts`

---

### 3. Récupérer les Contrats par Propriété
**GET** `http://localhost:8080/api/contracts/property/1`

---

### 4. Créer un Paiement
**POST** `http://localhost:8080/api/payments`

**Body (raw JSON):**
```json
{
  "contractId": 1,
  "amount": 2000.00,
  "dueDate": "2025-12-05",
  "type": "RENT"
}
```

**Type possibles :** `RENT`, `DEPOSIT`

---

### 5. Récupérer les Paiements par Contrat
**GET** `http://localhost:8080/api/payments/contract/1`

---

### 6. Créer un Locataire
**POST** `http://localhost:8080/api/tenants`

**Body (raw JSON):**
```json
{
  "contractId": 1,
  "userId": 1,
  "roomId": 1
}
```

---

### 7. Récupérer les Locataires par Contrat
**GET** `http://localhost:8080/api/tenants/contract/1`

---

### 8. Récupérer les Locataires par Utilisateur
**GET** `http://localhost:8080/api/tenants/user/1`

---

## 📝 Collection Postman

### Créer une Collection

1. Dans Postman, cliquez sur **"New"** → **"Collection"**
2. Nommez-la **"Collocation Microservices"**
3. Créez des dossiers pour chaque service :
   - Utilisateur Service
   - Annonce Service
   - Contrat Service

### Variables d'Environnement (Recommandé)

Créez un environnement avec ces variables :

**Nom de l'environnement :** `Local Development`

| Variable | Valeur Initiale |
|----------|----------------|
| `base_url` | `http://localhost:8080` |
| `gateway_url` | `http://localhost:8080` |
| `annonce_service` | `http://localhost:8082` |
| `contrat_service` | `http://localhost:8083` |
| `utilisateur_service` | `http://localhost:8084` |

**Utilisation dans les requêtes :**
```
{{base_url}}/api/users
{{gateway_url}}/api/properties
```

---

## 🔍 Vérification Rapide

### Test de Santé des Services

1. **Eureka Dashboard :** http://localhost:8761
   - Vérifiez que tous les services sont "UP"

2. **Test Gateway :**
   ```
   GET http://localhost:8080/api/users
   ```
   - Doit retourner une liste (vide ou avec données)

3. **Test Direct Service :**
   ```
   GET http://localhost:8082/api/properties
   GET http://localhost:8083/api/contracts
   GET http://localhost:8084/api/users
   ```

---

## ⚠️ Erreurs Courantes

### 503 Service Unavailable
- **Cause :** Service non démarré ou non enregistré dans Eureka
- **Solution :** Vérifiez que le service est démarré et visible sur http://localhost:8761

### 404 Not Found
- **Cause :** Route incorrecte ou service non accessible
- **Solution :** Vérifiez l'URL et que le Gateway route correctement

### 500 Internal Server Error
- **Cause :** Erreur dans le service (base de données, logique métier)
- **Solution :** Vérifiez les logs du service concerné

### Connection Refused
- **Cause :** Service non démarré
- **Solution :** Démarrez le service concerné

---

## 🎯 Scénario de Test Complet

### 1. Créer un Utilisateur
```
POST {{base_url}}/api/users
Body: { "email": "test@example.com", ... }
→ Sauvegarder l'ID retourné (ex: userId = 1)
```

### 2. Créer un Profil pour cet Utilisateur
```
POST {{base_url}}/api/profiles
Body: { "userId": 1, ... }
```

### 3. Créer une Propriété
```
POST {{base_url}}/api/properties
Body: { "title": "...", "ownerId": 1, ... }
→ Sauvegarder propertyId
```

### 4. Créer une Chambre
```
POST {{base_url}}/api/rooms
Body: { "propertyId": 1, ... }
→ Sauvegarder roomId
```

### 5. Créer une Annonce
```
POST {{base_url}}/api/ads
Body: { "propertyId": 1, "roomId": 1, ... }
```

### 6. Créer un Contrat
```
POST {{base_url}}/api/contracts
Body: { "propertyId": 1, ... }
→ Sauvegarder contractId
```

### 7. Créer un Locataire
```
POST {{base_url}}/api/tenants
Body: { "contractId": 1, "userId": 1, "roomId": 1 }
```

---

## 📊 Exemples de Réponses

### Réponse Succès (200 OK)
```json
{
  "id": 1,
  "email": "test@example.com",
  "nom": "Test",
  "prenom": "User",
  "telephone": "0612345678"
}
```

### Réponse Création (201 Created)
```json
{
  "id": 1,
  "title": "Appartement 3 pièces",
  "address": "123 Rue Example",
  "city": "Casablanca",
  "description": "Bel appartement...",
  "ownerId": 1
}
```

### Réponse Liste (200 OK)
```json
[
  {
    "id": 1,
    "email": "user1@example.com",
    ...
  },
  {
    "id": 2,
    "email": "user2@example.com",
    ...
  }
]
```

### Réponse Erreur (404 Not Found)
```json
{
  "timestamp": "2025-11-23T17:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found"
}
```

---

## 💡 Astuces Postman

1. **Sauvegarder les Réponses :** Cliquez sur "Save Response" pour garder des exemples
2. **Tests Automatiques :** Ajoutez des scripts de test dans l'onglet "Tests"
3. **Variables de Collection :** Utilisez `{{variable}}` pour réutiliser des valeurs
4. **Pré-requis Scripts :** Utilisez "Pre-request Script" pour générer des données dynamiques
5. **Environnements :** Créez différents environnements (Dev, Test, Prod)

---

**Bon test ! 🚀**

