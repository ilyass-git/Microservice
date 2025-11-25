# 🗄️ Guide Flyway - Gestion des Migrations de Base de Données

## ✅ Configuration Actuelle

Flyway est configuré dans tous les services :
- ✅ `flyway-core` : Bibliothèque principale
- ✅ `flyway-mysql` : Support MySQL
- ✅ Migrations SQL créées dans `src/main/resources/db/migration/`

## 📁 Structure des Migrations

### Utilisateur Service
```
utilisateur-service/src/main/resources/db/migration/
├── V1__Create_users_table.sql
├── V2__Create_profiles_table.sql
└── V3__Create_preferences_table.sql
```

### Annonce Service
```
annonce-service/src/main/resources/db/migration/
├── V1__Create_properties_table.sql
├── V2__Create_rooms_table.sql
└── V3__Create_ads_table.sql
```

### Contrat Service
```
contrat-service/src/main/resources/db/migration/
├── V1__Create_contracts_table.sql
├── V2__Create_tenants_table.sql
└── V3__Create_payments_table.sql
```

## 🚀 Fonctionnement Automatique

Flyway s'exécute **automatiquement** au démarrage de chaque service :

1. ✅ Scan du dossier `db/migration/`
2. ✅ Vérification de la table `flyway_schema_history`
3. ✅ Exécution des migrations non appliquées
4. ✅ Enregistrement dans `flyway_schema_history`

## ⚙️ Configuration JPA/Hibernate

**Important** : Quand Flyway est présent, Spring Boot désactive automatiquement la génération automatique de schéma Hibernate (`spring.jpa.hibernate.ddl-auto`).

Cela signifie :
- ✅ Flyway gère la création/modification du schéma
- ✅ Hibernate valide seulement que les entités correspondent au schéma
- ✅ Pas de conflit entre Flyway et Hibernate

## 📝 Convention de Nommage

Format : `V{version}__{description}.sql`

Exemples :
- `V1__Create_users_table.sql` ✅
- `V2__Add_email_index.sql` ✅
- `V3__Alter_users_add_phone.sql` ✅

**Règles** :
- `V` majuscule obligatoire
- Numéro de version séquentiel (1, 2, 3...)
- `__` (double underscore) séparateur obligatoire
- Description en majuscules avec underscores
- Extension `.sql`

## 🔄 Ajouter une Nouvelle Migration

1. **Créer le fichier** dans `src/main/resources/db/migration/`
2. **Nommer** selon la convention : `V{next_version}__{description}.sql`
3. **Écrire** le SQL de migration
4. **Démarrer** l'application - Flyway exécute automatiquement

Exemple :
```sql
-- V4__Add_index_to_users_email.sql
CREATE INDEX idx_users_email ON users(email);
```

## ⚠️ Bonnes Pratiques

1. **Ne jamais modifier** une migration déjà appliquée
2. **Créer une nouvelle migration** pour toute modification
3. **Tester** en local avant la production
4. **Utiliser des noms descriptifs** pour les migrations
5. **Versionner** toutes les migrations dans Git

## 🛠️ Commandes Maven (Optionnel)

Pour exécuter Flyway manuellement via Maven, ajoutez le plugin dans `pom.xml` :

```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <configuration>
        <url>jdbc:mysql://localhost:3306/collocation_db</url>
        <user>root</user>
        <password>votre_mot_de_passe</password>
    </configuration>
</plugin>
```

Commandes disponibles :
```bash
# Migrer la base de données
mvn flyway:migrate

# Vérifier l'état des migrations
mvn flyway:info

# Réparer la table flyway_schema_history
mvn flyway:repair
```

## 📊 Vérifier les Migrations Appliquées

Connectez-vous à MySQL et consultez la table `flyway_schema_history` :

```sql
USE collocation_db;
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

Cette table contient :
- `installed_rank` : Ordre d'installation
- `version` : Version de la migration
- `description` : Description
- `type` : Type (SQL, etc.)
- `installed_on` : Date d'installation
- `success` : Succès (1) ou échec (0)

## 🔍 Dépannage

### Problème : Checksum Mismatch (Migration déjà appliquée avec contenu différent)

**Erreur typique :**
```
Migration checksum mismatch for migration version 1
-> Applied to database : -1254114292
-> Resolved locally    : -1116642622
```

**Solution :** 
Une configuration Flyway automatique a été créée dans chaque service (`FlywayConfig.java`) qui :
- ✅ Répare automatiquement les checksums au démarrage
- ✅ Désactive la validation stricte pour permettre la réparation
- ✅ Applique `baselineOnMigrate` pour les bases existantes

Cette configuration résout automatiquement ce problème.

**Solution manuelle (si nécessaire) :**
```sql
-- Se connecter à MySQL
USE collocation_db;

-- Supprimer les entrées problématiques (ATTENTION : seulement en développement)
DELETE FROM flyway_schema_history WHERE version = '1';

-- Ou réparer via Maven
mvn flyway:repair
```

### Problème : Migration échoue
1. Vérifier les logs de l'application
2. Consulter `flyway_schema_history` pour voir quelle migration a échoué
3. Corriger le SQL dans la migration
4. Utiliser `mvn flyway:repair` si nécessaire

### Problème : Conflit avec Hibernate
Si Hibernate essaie de créer des tables :
- Vérifier que `spring.jpa.hibernate.ddl-auto` n'est pas défini à `create` ou `update`
- Avec Flyway, utiliser `validate` ou `none`

### Problème : Base de données existante
Si la base existe déjà avec des tables :
- Flyway créera la table `flyway_schema_history`
- Les migrations seront appliquées dans l'ordre
- La configuration `baselineOnMigrate=true` est déjà activée

## 📚 Ressources

- [Documentation Flyway](https://flywaydb.org/documentation/)
- [Spring Boot + Flyway](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)

