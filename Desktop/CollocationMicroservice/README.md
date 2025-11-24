# Collocation Microservices

Projet de microservices pour la gestion de collocations.

## Architecture

Le projet est composé de 4 services :

1. **eureka-server** (port 8761) - Service de découverte
2. **annonce-service** (port 8082) - Gestion des annonces, propriétés et chambres
3. **contrat-service** (port 8083) - Gestion des contrats, paiements et locataires
4. **utilisateur-service** (port 8084) - Gestion des utilisateurs, profils et préférences

## Prérequis

- Java 17
- Maven 3.6+
- MySQL (optionnel, pour la base de données)

## Comment lancer le serveur Eureka

### Option 1 : Via Maven (recommandé)

```bash
# Depuis la racine du projet
cd eureka-server
mvn spring-boot:run
```

### Option 2 : Via l'IDE

1. Ouvrez le projet dans votre IDE (IntelliJ IDEA, Eclipse, etc.)
2. Naviguez vers `eureka-server/src/main/java/emsi/ma/eurekaserver/EurekaServerApplication.java`
3. Clic droit → Run 'EurekaServerApplication'

### Option 3 : Via le wrapper Maven

```bash
cd eureka-server
./mvnw spring-boot:run
# Sur Windows :
mvnw.cmd spring-boot:run
```

## Accéder au dashboard Eureka

Une fois le serveur Eureka démarré, accédez au dashboard via :

**http://localhost:8761**

Vous verrez l'interface web d'Eureka où vous pourrez voir tous les services enregistrés.

## Lancer tous les microservices

### Ordre de démarrage recommandé :

1. **D'abord, lancez Eureka Server** (voir ci-dessus)

2. **Ensuite, lancez les microservices** (dans des terminaux séparés) :

```bash
# Terminal 1 - Annonce Service
cd annonce-service
mvn spring-boot:run

# Terminal 2 - Contrat Service
cd contrat-service
mvn spring-boot:run

# Terminal 3 - Utilisateur Service
cd utilisateur-service
mvn spring-boot:run
```

### Vérification

Après avoir lancé tous les services, retournez sur **http://localhost:8761** et vous devriez voir :

- **ANNONCE-SERVICE** (port 8082)
- **CONTRAT-SERVICE** (port 8083)
- **UTILISATEUR-SERVICE** (port 8084)

## Structure des DTOs et Mappers

### Annonce Service
- **DTOs** : `AdDto`, `PropertyDto`, `RoomDto`
- **Mappers** : `AdMapper`, `PropertyMapper`, `RoomMapper`

### Contrat Service
- **DTOs** : `ContractDto`, `PaymentDto`, `TenantDto`
- **Mappers** : `ContractMapper`, `PaymentMapper`, `TenantMapper`

### Utilisateur Service
- **DTOs** : `UserDto`, `UserCreateDto`, `UserResponseDto`, `ProfileDto`, `PreferenceDto`
- **Mappers** : `UserMapper`, `ProfileMapper`, `PreferenceMapper`

Tous les mappers utilisent MapStruct et sont configurés avec le modèle de composant Spring.

## Compilation

Pour compiler tous les modules :

```bash
# Depuis la racine du projet
mvn clean install
```

## Initialisation des Données de Test

Chaque service contient un script d'initialisation automatique (`DataInitializer`) qui :

- ✅ S'exécute automatiquement au démarrage
- ✅ Crée des données de test si la base est vide
- ✅ **Préserve les données existantes** - ne modifie jamais ce qui existe déjà
- ✅ Respecte vos modifications - les données modifiées ou supprimées restent telles quelles

**Données créées :**
- Utilisateur Service : 4 utilisateurs, 4 profils, 4 préférences
- Annonce Service : 3 propriétés, 4 chambres, 3 annonces
- Contrat Service : 3 contrats, 3 paiements, 3 locataires

## Notes

- Assurez-vous que MySQL est démarré si vous utilisez une base de données
- Les services partagent la même base de données `collocation_db` (configurable dans `application.properties`)
- Les ports peuvent être modifiés dans les fichiers `application.properties` de chaque service
- Les données de test sont créées automatiquement au premier démarrage

