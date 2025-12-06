package emsi.ma.utilisateurservice.config;

import emsi.ma.utilisateurservice.domain.entity.Preference;
import emsi.ma.utilisateurservice.domain.entity.Profile;
import emsi.ma.utilisateurservice.domain.entity.User;
import emsi.ma.utilisateurservice.repository.PreferenceRepository;
import emsi.ma.utilisateurservice.repository.ProfileRepository;
import emsi.ma.utilisateurservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PreferenceRepository preferenceRepository;

    @Override
    public void run(String... args) {
        log.info("Initialisation des données de test...");
        initializeUsers();
        initializeProfiles();
        initializePreferences();
        log.info("Initialisation des données terminée.");
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            log.info("Création des utilisateurs de test...");
            
            User user1 = User.builder()
                    .email("john.doe@example.com")
                    .password("password123")
                    .nom("Doe")
                    .prenom("John")
                    .telephone("0612345678")
                    .build();
            userRepository.save(user1);

            User user2 = User.builder()
                    .email("marie.martin@example.com")
                    .password("password123")
                    .nom("Martin")
                    .prenom("Marie")
                    .telephone("0623456789")
                    .build();
            userRepository.save(user2);

            User user3 = User.builder()
                    .email("pierre.dupont@example.com")
                    .password("password123")
                    .nom("Dupont")
                    .prenom("Pierre")
                    .telephone("0634567890")
                    .build();
            userRepository.save(user3);

            User user4 = User.builder()
                    .email("sophie.bernard@example.com")
                    .password("password123")
                    .nom("Bernard")
                    .prenom("Sophie")
                    .telephone("0645678901")
                    .build();
            userRepository.save(user4);

            log.info("4 utilisateurs créés.");
        } else {
            log.info("Des utilisateurs existent déjà. Aucune création.");
        }
    }

    private void initializeProfiles() {
        if (profileRepository.count() == 0) {
            log.info("Création des profils de test...");
            
            var users = userRepository.findAll();
            if (users.size() >= 4) {
                Profile profile1 = Profile.builder()
                        .userId(users.get(0).getId())
                        .bio("Étudiant en informatique, recherche colocation à Casablanca")
                        .age(22)
                        .avatarUrl("https://example.com/avatar1.jpg")
                        .build();
                profileRepository.save(profile1);

                Profile profile2 = Profile.builder()
                        .userId(users.get(1).getId())
                        .bio("Professionnelle, calme et organisée")
                        .age(28)
                        .avatarUrl("https://example.com/avatar2.jpg")
                        .build();
                profileRepository.save(profile2);

                Profile profile3 = Profile.builder()
                        .userId(users.get(2).getId())
                        .bio("Propriétaire d'appartement, recherche colocataires")
                        .age(35)
                        .avatarUrl("https://example.com/avatar3.jpg")
                        .build();
                profileRepository.save(profile3);

                Profile profile4 = Profile.builder()
                        .userId(users.get(3).getId())
                        .bio("Étudiante en médecine, non-fumeuse")
                        .age(24)
                        .avatarUrl("https://example.com/avatar4.jpg")
                        .build();
                profileRepository.save(profile4);

                log.info("4 profils créés.");
            }
        } else {
            log.info("Des profils existent déjà. Aucune création.");
        }
    }

    private void initializePreferences() {
        if (preferenceRepository.count() == 0) {
            log.info("Création des préférences de test...");
            
            var users = userRepository.findAll();
            if (users.size() >= 4) {
                Preference pref1 = Preference.builder()
                        .userId(users.get(0).getId())
                        .budget(new BigDecimal("2000.00"))
                        .city("Casablanca")
                        .smokingAllowed(false)
                        .build();
                preferenceRepository.save(pref1);

                Preference pref2 = Preference.builder()
                        .userId(users.get(1).getId())
                        .budget(new BigDecimal("3000.00"))
                        .city("Rabat")
                        .smokingAllowed(true)
                        .build();
                preferenceRepository.save(pref2);

                Preference pref3 = Preference.builder()
                        .userId(users.get(2).getId())
                        .budget(new BigDecimal("5000.00"))
                        .city("Casablanca")
                        .smokingAllowed(false)
                        .build();
                preferenceRepository.save(pref3);

                Preference pref4 = Preference.builder()
                        .userId(users.get(3).getId())
                        .budget(new BigDecimal("2500.00"))
                        .city("Fès")
                        .smokingAllowed(false)
                        .build();
                preferenceRepository.save(pref4);

                log.info("4 préférences créées.");
            }
        } else {
            log.info("Des préférences existent déjà. Aucune création.");
        }
    }
}






