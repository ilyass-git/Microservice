# 🚀 Guide de Démarrage des Services

## ✅ Ordre de Démarrage Recommandé

### 1. **Eureka Server** (Port 8761)
```bash
cd eureka-server
mvn spring-boot:run
```
**Vérification** : http://localhost:8761

### 2. **Config Server** (Port 8888)
```bash
cd config-server
mvn spring-boot:run
```
**Vérification** : http://localhost:8888/actuator/health

### 3. **Utilisateur Service** (Port 8084)
```bash
cd utilisateur-service
mvn spring-boot:run
```
**Vérification** : http://localhost:8084/api/users

### 4. **Annonce Service** (Port 8082)
```bash
cd annonce-service
mvn spring-boot:run
```
**Vérification** : http://localhost:8082/api/properties

### 5. **Contrat Service** (Port 8083)
```bash
cd contrat-service
mvn spring-boot:run
```
**Vérification** : http://localhost:8083/api/contracts

### 6. **Gateway Service** (Port 8080) - Optionnel
```bash
cd gateway-service
mvn spring-boot:run
```
**Vérification** : http://localhost:8080/actuator/health

---

## 📊 État Actuel des Services

### ✅ Services Actifs
- ✅ **Eureka Server** (8761) - Service Discovery
- ✅ **Config Server** (8888) - Configuration centralisée
- ✅ **Utilisateur Service** (8084) - Gestion des utilisateurs
- ✅ **Annonce Service** (8082) - Gestion des annonces
- ✅ **Gateway Service** (8080) - API Gateway

### ⏳ Services en Démarrage
- ⏳ **Contrat Service** (8083) - Peut prendre 1-2 minutes

### ❌ Services Non Démarrés
- ❌ **Kafka** (9092) - Pour les événements asynchrones

---

## 🔄 Communications Vérifiées

### ✅ OpenFeign (Synchrones)
- ✅ Annonce Service → Utilisateur Service
- ✅ Contrat Service → Utilisateur Service (quand actif)
- ✅ Contrat Service → Annonce Service (Property) (quand actif)
- ✅ Contrat Service → Annonce Service (Room) (quand actif)

### ⏳ Kafka (Asynchrones)
- ⏳ Topic `contract-events` - Nécessite Kafka démarré

---

## 🐳 Démarrer Kafka

### Option 1 : Docker (Recommandé)
```bash
docker run -d --name kafka \
  -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \
  apache/kafka:latest
```

### Option 2 : Docker Compose
Créez un fichier `docker-compose.yml` :
```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: apache/kafka:latest
    ports:
      - "9092:9092"
    depends_on:
      - zookeeper
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
```

Puis :
```bash
docker-compose up -d
```

### Option 3 : Installation Locale
1. Télécharger Kafka depuis https://kafka.apache.org/downloads
2. Démarrer Zookeeper :
   ```bash
   bin/zookeeper-server-start.sh config/zookeeper.properties
   ```
3. Démarrer Kafka :
   ```bash
   bin/kafka-server-start.sh config/server.properties
   ```

---

## ✅ Vérification du Fonctionnement

### 1. Vérifier Eureka
Ouvrir : http://localhost:8761

Vous devriez voir :
- CONFIG-SERVER
- UTILISATEUR-SERVICE
- ANNONCE-SERVICE
- CONTRAT-SERVICE (quand démarré)
- GATEWAY-SERVICE

### 2. Tester les Endpoints

#### Utilisateur Service
```bash
# Lister les utilisateurs
curl http://localhost:8084/api/users

# Récupérer un utilisateur
curl http://localhost:8084/api/users/1
```

#### Annonce Service
```bash
# Lister les propriétés
curl http://localhost:8082/api/properties

# Lister les chambres
curl http://localhost:8082/api/rooms

# Récupérer une propriété
curl http://localhost:8082/api/properties/1
```

#### Contrat Service (quand démarré)
```bash
# Lister les contrats
curl http://localhost:8083/api/contracts

# Lister les tenants
curl http://localhost:8083/api/tenants
```

### 3. Tester les Communications OpenFeign

#### Test : Créer un Contrat (vérifie la propriété)
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

**Vérifier les logs** : Vous devriez voir :
```
🔗 [COMMUNICATION INTER-SERVICE] Vérification de l'existence de la propriété ID: 1
✅ [COMMUNICATION RÉUSSIE] Propriété trouvée: ...
```

#### Test : Créer un Tenant (vérifie utilisateur + chambre)
```bash
curl -X POST http://localhost:8083/api/tenants \
  -H "Content-Type: application/json" \
  -d '{
    "contractId": 1,
    "userId": 1,
    "roomId": 1
  }'
```

**Vérifier les logs** : Vous devriez voir :
```
🔗 [COMMUNICATION INTER-SERVICE] Vérification de l'existence de l'utilisateur ID: 1
✅ [COMMUNICATION RÉUSSIE] Utilisateur trouvé: ...
🔗 [COMMUNICATION INTER-SERVICE] Vérification de la chambre ID: 1
✅ [COMMUNICATION RÉUSSIE] Chambre trouvée: ...
🔄 [MISE À JOUR] Marquage de la chambre ID 1 comme non disponible
```

### 4. Vérifier Kafka (si démarré)

#### Vérifier que Kafka est actif
```bash
# Windows PowerShell
Test-NetConnection -ComputerName localhost -Port 9092

# Linux/Mac
telnet localhost 9092
```

#### Consulter les messages du topic
```bash
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic contract-events \
  --from-beginning
```

---

## 🐛 Dépannage

### Problème : Service ne démarre pas

**Solutions** :
1. Vérifier les logs dans la console
2. Vérifier que les services précédents sont démarrés
3. Vérifier la base de données MySQL
4. Vérifier les ports (pas de conflit)

### Problème : Service ne s'enregistre pas dans Eureka

**Solutions** :
1. Vérifier que Eureka Server est démarré
2. Vérifier la configuration dans `application.properties`
3. Attendre 30-60 secondes (enregistrement asynchrone)

### Problème : Communication OpenFeign échoue

**Solutions** :
1. Vérifier que le service cible est démarré
2. Vérifier Eureka : http://localhost:8761
3. Vérifier le nom du service dans `@FeignClient(name = "...")`

### Problème : Kafka ne fonctionne pas

**Solutions** :
1. Vérifier que Kafka est démarré : `Test-NetConnection localhost 9092`
2. Vérifier les logs des services (erreurs de connexion)
3. Vérifier la configuration dans `KafkaProducerConfig` et `KafkaConsumerConfig`

---

## 📝 Commandes Utiles

### Vérifier les ports en écoute
```powershell
# Windows PowerShell
netstat -ano | findstr "LISTENING" | findstr "8761 8888 8080 8082 8083 8084 9092"
```

### Arrêter tous les services Java
```powershell
# Windows PowerShell
Get-Process java | Stop-Process
```

### Vérifier les processus Java
```powershell
Get-Process | Where-Object {$_.ProcessName -like "*java*"}
```

---

## ✅ Checklist de Démarrage

- [ ] MySQL démarré et base `collocation_db` créée
- [ ] Eureka Server démarré (8761)
- [ ] Config Server démarré (8888)
- [ ] Utilisateur Service démarré (8084)
- [ ] Annonce Service démarré (8082)
- [ ] Contrat Service démarré (8083)
- [ ] Gateway Service démarré (8080) - Optionnel
- [ ] Kafka démarré (9092) - Pour les événements
- [ ] Tous les services enregistrés dans Eureka
- [ ] Tests des endpoints réussis
- [ ] Tests des communications OpenFeign réussis

---

**Les services sont maintenant démarrés et prêts à être utilisés ! 🎉**

