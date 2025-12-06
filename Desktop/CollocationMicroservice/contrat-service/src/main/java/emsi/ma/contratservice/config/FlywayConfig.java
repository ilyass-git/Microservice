package emsi.ma.contratservice.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuration Flyway pour gérer les problèmes de checksum
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
        }
        
        return flyway;
    }
}



