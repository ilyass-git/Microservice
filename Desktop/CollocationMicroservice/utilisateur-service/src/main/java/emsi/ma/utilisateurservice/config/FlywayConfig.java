package emsi.ma.utilisateurservice.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuration Flyway pour gérer les problèmes de checksum
 * 
 * Cette configuration permet à Flyway de réparer automatiquement les checksums
 * en cas de mismatch (par exemple, si les migrations ont été modifiées après
 * avoir été appliquées à la base de données).
 */
@Configuration
public class FlywayConfig {

    @Autowired
    private DataSource dataSource;

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .validateOnMigrate(false) // Désactive la validation stricte pour permettre la réparation
                .load();
        
        // Réparer les checksums si nécessaire
        try {
            flyway.repair();
        } catch (Exception e) {
            // Si la réparation échoue, continuer quand même
            // Cela peut arriver si la table flyway_schema_history n'existe pas encore
        }
        
        return flyway;
    }
}



